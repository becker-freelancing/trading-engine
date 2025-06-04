package com.becker.freelance.management.api.environment;

import java.time.LocalDateTime;

public interface TimeChangeListener {

    public void onTimeChange(LocalDateTime newTime);
}
