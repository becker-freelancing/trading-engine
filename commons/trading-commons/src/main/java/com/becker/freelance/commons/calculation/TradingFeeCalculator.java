package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.service.ExtServiceLoader;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.math.Decimal;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface TradingFeeCalculator {

    public static TradingFeeCalculator getInstance() {
        Optional<TradingFeeCalculator> tradingFeeCalculator = ExtServiceLoader.tryLoadSingle(TradingFeeCalculator.class);
        return tradingFeeCalculator.orElse(TradingFeeCalculator.fromConfigFile());
    }

    public static TradingFeeCalculator fromConfigFile() {
        InputStream resource = TradingFeeCalculator.class.getClassLoader().getResourceAsStream("fee-config.json");
        JSONObject config = null;
        try {
            config = new JSONObject(new String(resource.readAllBytes()));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read file fee-config.json", e);
        }

        Decimal makerFeeRate = new Decimal(config.getDouble("makerFeeRate"));
        Decimal takerFeeRate = new Decimal(config.getDouble("takerFeeRate"));

        return new TradingFeeCalculator() {
            @Override
            public Decimal calculateMakerTradingFeeInCounterCurrency(Decimal currentPrice, Decimal positionSize) {
                return currentPrice.multiply(makerFeeRate).multiply(positionSize);
            }

            @Override
            public Decimal calculateTakerTradingFeeInCounterCurrency(Decimal currentPrice, Decimal positionSize) {
                return currentPrice.multiply(takerFeeRate).multiply(positionSize);
            }
        };
    }

    public Decimal calculateMakerTradingFeeInCounterCurrency(Decimal currentPrice, Decimal positionSize);

    public Decimal calculateTakerTradingFeeInCounterCurrency(Decimal currentPrice, Decimal positionSize);

    public default Decimal calculateTradingFeeInCounterCurrency(EntrySignal entrySignal) {
        return calculateTradingFeeInCounterCurrency(entrySignal, entrySignal.size());
    }


    public default Decimal calculateTradingFeeInCounterCurrency(EntrySignal entrySignal, Decimal size) {
        throw new UnsupportedOperationException();
//        if (entrySignal.isOpenTaker()) {
//            return calculateTakerTradingFeeInCounterCurrency(entrySignal.openPrice().getCloseMid(), size);
//        }
//        return calculateMakerTradingFeeInCounterCurrency(entrySignal.openPrice().getCloseMid(), size);
    }
}
