package com.becker.freelance.data;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Synchronizer {

    void addSubscriber(Synchronizeable synchronizeable);

    void addPrioritySubscriber(Synchronizeable synchronizeable);

    Optional<LocalDateTime> minTime();

    Optional<LocalDateTime> maxTime();
}
