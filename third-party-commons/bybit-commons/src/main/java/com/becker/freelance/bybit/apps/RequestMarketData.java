package com.becker.freelance.bybit.apps;

import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.market.MarketInterval;
import com.bybit.api.client.domain.market.request.MarketDataRequest;
import com.bybit.api.client.restApi.BybitApiMarketRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestMarketData {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        String symbol = "LTCUSDT";
//        Function<String, String> transformator = (in) -> {
//            double price = Double.parseDouble(in);
//            price = new BigDecimal(1 / price).setScale(5, RoundingMode.HALF_UP).doubleValue();
//            return String.valueOf(price);
//        };

        Function<String, String> transformator = in -> in;

        Path path = Path.of(getBasePath().toString(), "data-bybit", symbol + "_1.csv");
        System.out.println(path);
        createPath(path);

        BybitApiMarketRestClient marketRestClient = BybitApiClientFactory.newInstance().newMarketDataRestClient();

        LocalDateTime endTime = getMaxTime(path);
        LocalDateTime startTime = endTime;

        int i = 0;
        Stream<String> lines = Stream.of();
        while (true) {
            startTime = endTime.minusMinutes(998);
            System.out.println("Requesting from " + startTime + " to " + endTime);
            Map<String, Object> marketLinesData;
            try {

                marketLinesData = (Map<String, Object>) marketRestClient.getMarketLinesData(MarketDataRequest.builder()
                        .category(CategoryType.LINEAR)
                        .symbol(symbol)
                        .marketInterval(MarketInterval.ONE_MINUTE)
                        .start(map(startTime))
                        .end(map(endTime))
                        .limit(9999)
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            if (!"OK".equals(marketLinesData.get("retMsg"))) {
                System.out.println(marketLinesData.get("retMsg"));
                break;
            }

            Map<String, Object> result = (Map<String, Object>) marketLinesData.get("result");
            List<List<String>> list = (List<List<String>>) result.get("list");

            System.out.println("Received " + list.size());
            if (list.isEmpty()) {
                return;
            }

            lines = Stream.concat(lines, mapMarketData(list, transformator));

            i++;
            if (i % 10 == 0) {
                write(path, lines);
                lines = Stream.of();
            }

            endTime = startTime;
        }

        write(path, lines);
        JOptionPane.showInputDialog("FINISHED");
    }

    private static Stream<String> mapMarketData(List<List<String>> list, Function<String, String> priceTransformator) {
        return list.stream()
                .map(data -> map(data, priceTransformator));
    }

    private static String map(List<String> data, Function<String, String> priceTransformator) {
        LocalDateTime start = map(data.get(0));
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                start.format(formatter),
                priceTransformator.apply(data.get(1)), priceTransformator.apply(data.get(1)),
                priceTransformator.apply(data.get(2)), priceTransformator.apply(data.get(2)),
                priceTransformator.apply(data.get(3)), priceTransformator.apply(data.get(3)),
                priceTransformator.apply(data.get(4)), priceTransformator.apply(data.get(4)),
                data.get(5));
    }

    private static LocalDateTime map(String timeMs) {
        return Instant.ofEpochMilli(Long.parseLong(timeMs))
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private static void write(Path path, Stream<String> lines) throws IOException {
        String append = "\n" + lines.collect(Collectors.joining("\n"));
        Files.writeString(path, append, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    private static Long map(LocalDateTime time) {
        return time
                .toInstant(ZoneId.systemDefault().getRules().getOffset(time))
                .toEpochMilli();
    }

    private static LocalDateTime getMaxTime(Path path) throws IOException {
        return Files.lines(path)
                .skip(1)
                .map(s -> s.split(","))
                .map(s -> s[0])
                .map(datetime -> LocalDateTime.parse(datetime, formatter))
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.now());
    }

    private static void createPath(Path path) throws IOException {
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        if (!Files.exists(path)) {
            Files.createFile(path);
            Files.writeString(path, "closeTime,openBid,openAsk,highBid,highAsk,lowBid,lowAsk,closeBid,closeAsk,volume");
        }
    }

    public static Path getBasePath() {
        String appDataPath = System.getenv("APPDATA");

        if (appDataPath == null || appDataPath.isEmpty()) {
            String userHome = System.getProperty("user.home");
            appDataPath = Paths.get(userHome, ".config").toString();
        }

        return Path.of(appDataPath, "krypto-java");
    }
}
