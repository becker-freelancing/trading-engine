package com.becker.freelance.commons;

import java.time.LocalDateTime;

public record AppConfiguration(AppMode appMode, LocalDateTime applicationStartTime) {

}
