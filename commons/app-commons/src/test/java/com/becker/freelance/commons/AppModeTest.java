package com.becker.freelance.commons;

import com.becker.freelance.commons.app.AppMode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppModeTest {


    @Test
    void findAll() {
        List<AppMode> all = AppMode.findAll();

        assertEquals(2, all.size());
        assertEquals(Set.of("MOCK", "MOCK2"), all.stream().map(AppMode::getDataSourceName).collect(Collectors.toSet()));
    }

    @Test
    void fromDescription() {

        AppMode appMode = AppMode.fromDescription("MOCK_DEMO");

        assertEquals("MOCK", appMode.getDataSourceName());
    }

    @Test
    void fromDescriptionForNonExisting() {

        assertThrows(IllegalArgumentException.class, () -> AppMode.fromDescription("NON_EXISTING"));

    }
}