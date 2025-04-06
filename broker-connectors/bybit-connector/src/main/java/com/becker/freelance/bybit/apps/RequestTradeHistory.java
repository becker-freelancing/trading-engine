package com.becker.freelance.bybit.apps;

import com.becker.freelance.bybit.env.EnvironmentProvider;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestTradeHistory {

    public static void main(String[] args) throws IOException {
        LocalDateTime exportStart = LocalDateTime.parse("2025-04-01T07:50:00");
        LocalDateTime exportEnd = LocalDateTime.parse("2025-04-05T00:00:00");


        String fileName = "pnls-" + exportStart + "-" + exportEnd + ".csv";
        Path path = Path.of(getBasePath().toString(), "export", "bybit", fileName.replaceAll(":", "_"));
        EnvironmentProvider environmentProvider = new EnvironmentProvider();
        BybitApiClientFactory bybitApiClientFactory = BybitApiClientFactory.newInstance(environmentProvider.apiKey(), environmentProvider.secret(), environmentProvider.baseURL());

        BybitApiPositionRestClient bybitApiPositionRestClient = bybitApiClientFactory.newPositionRestClient();

        Stream<Pnl> pnls = Stream.of();
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
        PositionDataRequest.PositionDataRequestBuilder positionDataRequestBuilder = PositionDataRequest.builder().category(CategoryType.LINEAR);

        if (cursor == null) {
            positionDataRequestBuilder = positionDataRequestBuilder
                    .startTime(startMillis)
                    .endTime(endMillis);
        }

        if (cursor != null) {
            positionDataRequestBuilder = positionDataRequestBuilder.cursor(cursor);
        }

        Map<String, Object> closePnlList = (Map<String, Object>) bybitApiPositionRestClient.getClosePnlList(positionDataRequestBuilder
                .build());

        if (0 != (int) closePnlList.get("retCode")) {
            System.err.println(closePnlList.get("retMsg"));
        }

        Map<String, Object> result = (Map<String, Object>) closePnlList.get("result");
        List<Map<String, String>> list = (List<Map<String, String>>) result.get("list");


        pnls = Stream.concat(pnls, list.stream()
                .map(RequestTradeHistory::toPnl));
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
