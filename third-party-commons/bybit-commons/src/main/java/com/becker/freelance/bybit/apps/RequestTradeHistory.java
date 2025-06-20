package com.becker.freelance.bybit.apps;

import com.becker.freelance.bybit.env.BybitEnvironmentProvider;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.position.request.PositionDataRequest;
import com.bybit.api.client.restApi.BybitApiPositionRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestTradeHistory {

    public static void main(String[] args) throws IOException {
        LocalDateTime exportStart = LocalDateTime.parse("2025-06-18T07:00:00");
        LocalDateTime exportEnd = LocalDateTime.parse("2025-06-20T09:00:00");


        String fileName = "pnls-" + exportStart + "-" + exportEnd + ".csv";
        Path path = Path.of(getBasePath().toString(), "export", "bybit", fileName.replaceAll(":", "_"));
        BybitEnvironmentProvider bybitEnvironmentProvider = new BybitEnvironmentProvider();
        BybitApiClientFactory bybitApiClientFactory = BybitApiClientFactory.newInstance(bybitEnvironmentProvider.apiKey(), bybitEnvironmentProvider.secret(), bybitEnvironmentProvider.baseURL());

        BybitApiPositionRestClient bybitApiPositionRestClient = bybitApiClientFactory.newPositionRestClient();

        Stream<Pnl> pnls = Stream.of();
        exportStart = exportStart.minusHours(2);
        while (exportStart.isBefore(exportEnd)) {
            long startMillis = exportStart.toInstant(ZoneOffset.UTC).toEpochMilli();
            LocalDateTime currEnd = exportStart.plusHours(1);
            long endMillis = currEnd.toInstant(ZoneOffset.UTC).toEpochMilli();
            System.out.println("Requesting " + exportStart + " - " + currEnd);


            pnls = requestData(bybitApiPositionRestClient, startMillis, endMillis, pnls, null);

            exportStart = currEnd;
        }


        Path parent = path.getParent();
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        String csv = pnls.distinct()
                .sorted(Comparator.comparing(Pnl::createdTime))
                .map(pnl -> {
                    return String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s",
                            pnl.symbol, pnl.orderType, pnl.leverage, pnl.side, pnl.qty, pnl.createdTime, pnl.pnl, pnl.entryPrice, pnl.exitPrice, pnl.correct ? "T" : "F", pnl.id);
                })
                .collect(Collectors.joining("\n"));
        csv = "Symbol;orderType;Leverage;Side;Qty;CreateTime;Pnl;EntryPrice;ExitPrice;CourseDirection;Id\n" + csv;

        Files.writeString(path, csv);
        System.out.println("Saved to " + path);
    }

    @NotNull
    private static Stream<Pnl> requestData(BybitApiPositionRestClient bybitApiPositionRestClient, long startMillis, long endMillis, Stream<Pnl> pnls, String cursor) {

        do {
            PositionDataRequest.PositionDataRequestBuilder builder = PositionDataRequest.builder()
                    .category(CategoryType.LINEAR);

            if (cursor == null) {
                builder = builder.startTime(startMillis).endTime(endMillis).limit(200);
            } else {
                builder = builder.cursor(cursor);
            }

            Map<String, Object> response = (Map<String, Object>) bybitApiPositionRestClient.getClosePnlList(builder.build());

            if ((int) response.get("retCode") != 0) {
                System.err.println(response.get("retMsg"));
                break;
            }

            Map<String, Object> result = (Map<String, Object>) response.get("result");
            List<Map<String, String>> list = (List<Map<String, String>>) result.get("list");

            pnls = Stream.concat(pnls, list.stream().map(RequestTradeHistory::toPnl));

            cursor = (String) result.get("nextPageCursor");

        } while (cursor != null && !cursor.isEmpty());

        return pnls;
    }

    private static Pnl toPnl(Map<String, String> stringStringMap) {
        return new Pnl(
                stringStringMap.get("symbol"),
                stringStringMap.get("orderType"),
                stringStringMap.get("leverage").replaceAll("\\.", ","),
                stringStringMap.get("side"),
                stringStringMap.get("qty").replaceAll("\\.", ","),
                Instant.ofEpochMilli(Long.valueOf(stringStringMap.get("createdTime"))).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                stringStringMap.get("closedPnl").replaceAll("\\.", ","),
                stringStringMap.get("orderId"),
                stringStringMap.get("avgEntryPrice").replaceAll("\\.", ","),
                stringStringMap.get("avgExitPrice").replaceAll("\\.", ","),
                switch (stringStringMap.get("side")) {
                    case "Buy" ->
                            Double.parseDouble(stringStringMap.get("avgEntryPrice")) < Double.parseDouble(stringStringMap.get("avgExitPrice"));
                    case "Sell" ->
                            Double.parseDouble(stringStringMap.get("avgEntryPrice")) > Double.parseDouble(stringStringMap.get("avgExitPrice"));
                    default -> throw new IllegalStateException("Unexpected value: " + stringStringMap.get("side"));
                }
        );
    }

    public static Path getBasePath() {
        String appDataPath = System.getenv("APPDATA");

        if (appDataPath == null || appDataPath.isEmpty()) {
            String userHome = System.getProperty("user.home");
            appDataPath = Paths.get(userHome, ".config").toString();
        }

        return Path.of(appDataPath, "krypto-java");
    }

    private static record Pnl(String symbol, String orderType, String leverage, String side, String qty,
                              LocalDateTime createdTime, String pnl, String id, String entryPrice, String exitPrice,
                              Boolean correct) {


        @Override
        public boolean equals(Object obj) {
            return ((Pnl) obj).id().equals(id());
        }
    }
}
