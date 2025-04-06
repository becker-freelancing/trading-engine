package com.becker.freelance.data;

import com.becker.freelance.backtest.util.PathUtil;
import com.becker.freelance.broker.orderbook.Orderbook;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

public class OrderBookListener implements com.becker.freelance.broker.orderbook.OrderBookListener {

    private Path writePath;

    public OrderBookListener(Pair pair) {
        writePath = Path.of(PathUtil.fromRelativePath("data-bybit/orderbook/" + pair.baseCurrency() + pair.counterCurrency() + "_" + pair.timeInMinutes() + ".csv"));
        try {

            if (!Files.exists(writePath.getParent())) {
                Files.createDirectories(writePath.getParent());
            }
            if (!Files.exists(writePath)) {
                Files.createFile(writePath);
            }
            writeHeader();
        } catch (Exception e) {
            throw new IllegalStateException("Could not create file", e);
        }
    }

    private void writeHeader() throws IOException {
        long count = Files.lines(writePath).count();
        if (count == 0) {
            Files.writeString(writePath, "pair;time;type;bidValues;bidQuantities;askValues;askQuantities\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        }
    }

    @Override
    public void onOrderbook(Orderbook orderbook) {
        String format = String.format("%s;%s;%s;[%s];[%s];[%s];[%s]\n",
                orderbook.pair().technicalName(), orderbook.time(), orderbook.type(),
                orderbook.bidValue().stream().map(Decimal::toPlainString).collect(Collectors.joining(",")),
                orderbook.bidQuantity().stream().map(Decimal::toPlainString).collect(Collectors.joining(",")),
                orderbook.askValue().stream().map(Decimal::toPlainString).collect(Collectors.joining(",")),
                orderbook.askQuantity().stream().map(Decimal::toPlainString).collect(Collectors.joining(","))
        );

        try {
            Files.writeString(writePath, format, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write Orderbook", e);
        }
    }

    @Override
    public Pair supportedPair() {
        return null;
    }
}
