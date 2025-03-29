package com.becker.freelance.commons.position;


import com.becker.freelance.commons.signal.AmountEntrySignal;
import com.becker.freelance.commons.signal.DistanceEntrySignal;
import com.becker.freelance.commons.signal.LevelEntrySignal;

public interface PositionFactory {

    public StopLimitPosition createStopLimitPosition(LevelEntrySignal entrySignal);

    public TrailingPosition createTrailingPosition(LevelEntrySignal entrySignal);

    public StopLimitPosition createStopLimitPosition(DistanceEntrySignal entrySignal);

    public TrailingPosition createTrailingPosition(DistanceEntrySignal entrySignal);

    public StopLimitPosition createStopLimitPosition(AmountEntrySignal entrySignal);

    public TrailingPosition createTrailingPosition(AmountEntrySignal entrySignal);
}
