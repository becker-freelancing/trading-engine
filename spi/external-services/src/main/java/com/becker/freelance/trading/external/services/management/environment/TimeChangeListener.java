package com.becker.freelance.trading.external.services.management.environment;

import java.time.LocalDateTime;

public interface TimeChangeListener {

    public void onTimeChange(LocalDateTime newTime);
}
