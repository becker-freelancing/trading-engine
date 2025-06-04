package com.becker.freelance.commons.position;


import com.becker.freelance.commons.signal.EntrySignal;

public interface PositionFactory {

    public StopLimitPosition createStopLimitPosition(EntrySignal entrySignal);

    public TrailingPosition createTrailingPosition(EntrySignal entrySignal);

}
