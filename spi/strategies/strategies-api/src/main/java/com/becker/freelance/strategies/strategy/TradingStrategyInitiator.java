package com.becker.freelance.strategies.strategy;

import java.time.LocalDateTime;

public interface TradingStrategyInitiator {
    void initiate(Initializable initializable, LocalDateTime currentTime);
}
