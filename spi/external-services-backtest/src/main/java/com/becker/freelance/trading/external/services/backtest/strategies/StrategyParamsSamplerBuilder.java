package com.becker.freelance.trading.external.services.backtest.strategies;

import com.becker.freelance.backtest.configuration.BacktestStage;
import com.becker.freelance.trading.external.services.registry.SupportableExternalServiceBuilder;

public interface StrategyParamsSamplerBuilder extends SupportableExternalServiceBuilder<StrategyParamsSamplerBuilderParams, BacktestStage, StrategyParamsSampler> {
}
