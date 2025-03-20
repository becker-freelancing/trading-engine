package com.becker.freelance.data;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Synchronizer {

    public void addSubscibor(Synchronizeable synchronizeable);

    public Optional<LocalDateTime> minTime();

    public Optional<LocalDateTime> maxTime();
}
