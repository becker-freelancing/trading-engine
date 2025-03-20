package com.becker.freelance.data;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Synchronizer {

    void addSubscibor(Synchronizeable synchronizeable);

    Optional<LocalDateTime> minTime();

    Optional<LocalDateTime> maxTime();
}
