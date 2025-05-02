package com.becker.freelance.commons.app;

import java.time.LocalDateTime;

public record AppConfiguration(AppMode appMode, LocalDateTime applicationStartTime) {

}
