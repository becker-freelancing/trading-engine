package com.becker.freelance.execution;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.engine.StrategyEngine;
import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.strategies.strategy.TradingStrategyInitiator;
import com.becker.freelance.trading.external.services.candles.PriceRequestorBroker;
import com.becker.freelance.trading.external.services.candles.PriceRequestorBrokerBuilder;
import com.becker.freelance.trading.external.services.management.environment.TimeChangeListener;
import com.becker.freelance.trading.external.services.registry.ExternalServiceRegistry;
import com.becker.freelance.trading.external.services.remote.broker.RemoteAccountBalanceRequestor;
import com.becker.freelance.trading.external.services.remote.broker.RemoteAccountBalanceRequestorBuilder;
import com.becker.freelance.trading.external.services.remote.candles.RemoteCandleDataSource;
import com.becker.freelance.trading.external.services.remote.candles.RemoteCandleDataSourceBuilder;
import com.becker.freelance.trading.external.services.remote.candles.RemoteCandleSourceBuilderParams;
import com.becker.freelance.trading.external.services.remote.tradeexecution.RemoteTradeExecutorBuildParams;
import com.becker.freelance.trading.external.services.remote.tradeexecution.RemoteTradeExecutorBuilder;
import com.becker.freelance.trading.external.services.tradeexecution.TradeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class RemoteExecutionExecutor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RemoteExecutionExecutor.class);

    private final StrategySupplier strategySupplier;
    private final Pair pair;

    public RemoteExecutionExecutor(StrategyWithPair baseStrategy) {
        this.strategySupplier = baseStrategy.strategySupplier();
        this.pair = baseStrategy.pair();
    }

    @Override
    public void run() {
        try {

            ExternalServiceRegistry externalServiceRegistry = ExternalServiceRegistry.globalServiceRegistry();
            RemoteCandleDataSourceBuilder dataSourceBuilder = externalServiceRegistry.requireServiceBuilder(RemoteCandleDataSourceBuilder.class);

            RemoteTradeExecutorBuilder tradeExecutorBuilder = externalServiceRegistry.requireServiceBuilder(RemoteTradeExecutorBuilder.class);
            TradeExecutor tradeExecutor = tradeExecutorBuilder.build(new RemoteTradeExecutorBuildParams(
                    pair,
                    dataSourceBuilder.createEuroUsdRequestor()
            ));

            RemoteAccountBalanceRequestor accountBalanceRequestor = externalServiceRegistry.requireServiceBuilder(RemoteAccountBalanceRequestorBuilder.class).build();

            RemoteCandleDataSource candleDataSource = dataSourceBuilder.build(new RemoteCandleSourceBuilderParams(pair));
            PriceRequestorBroker priceRequestorBroker = externalServiceRegistry.requireServiceBuilder(PriceRequestorBrokerBuilder.class).build();

            TradingStrategyInitiator strategyInitiator = getStrategyInitiator(priceRequestorBroker);


            Set<TimeChangeListener> timeChangeListeners = new HashSet<>();

            StrategyEngine strategyEngine = new StrategyEngine(
                    pair,
                    strategySupplier,
                    tradeExecutor,
                    dataSourceBuilder.createEuroUsdRequestor(),
                    priceRequestorBroker,
                    timeChangeListeners::add,
                    strategyInitiator,
                    accountBalanceRequestor,
                    externalServiceRegistry.newScopedExternalServiceRegistry(),
                    () -> {
                        throw new UnsupportedOperationException("Not implemented yet");
                    });

            StrategyDataSubscriber strategyDataSubscriber = new StrategyDataSubscriber(strategyEngine, timeChangeListeners);
            candleDataSource.addSubscriber(strategyDataSubscriber);
        } catch (Exception e) {
            logger.error("Error while executing Strategy", e);
        }
    }

    private TradingStrategyInitiator getStrategyInitiator(PriceRequestorBroker priceRequestorBroker) {
//        BiConsumer<TradingStrategy, LocalDateTime> strategyInitiator = (tradingStrategy, currentTime) -> {
//            int requiredBarCount = tradingStrategy.unstableBars();
//            Pair strategyPair = tradingStrategy.getPair();
//            long barLengthInMinutes = strategyPair.toDuration().toMinutes();
//            List<TimeSeriesEntry> initiationData = priceRequestorBroker.forPair(strategyPair).getPriceInRange(
//                    currentTime.minusMinutes(barLengthInMinutes * requiredBarCount + 100),
//                    currentTime.minusMinutes(barLengthInMinutes));
//            TimeSeries timeSeries = new CompleteTimeSeries(pair, initiationData);
//            tradingStrategy.processInitData(timeSeries);
//        };
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
