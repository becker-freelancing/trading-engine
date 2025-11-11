package com.becker.freelance.trading.external.services.strategies;

import com.becker.freelance.trading.external.services.registry.ExternalService;

import java.time.LocalDateTime;

public interface MissingDataHandler extends ExternalService {

    public boolean shouldResetStrategy();

    public void onMissingData(LocalDateTime time);

    public void onDataReceived(LocalDateTime time);
}
