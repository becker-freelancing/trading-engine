package com.becker.freelance.commons;

import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.app.AppMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppConfigurationTest {

    AppConfiguration configuration;
    AppMode appMode;
    LocalDateTime startTime;

    @BeforeEach
    void setUp() {
        appMode = Mockito.mock(AppMode.class);
        startTime = LocalDateTime.of(2020, 1, 1, 0, 0);
        configuration = new AppConfiguration(
                appMode, startTime
        );
    }

    @Test
    void appMode() {
        assertEquals(appMode, configuration.appMode());
    }

    @Test
    void startTime() {
        assertEquals(startTime, configuration.applicationStartTime());
    }
}