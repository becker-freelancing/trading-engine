package com.becker.freelance.indicators.ta.swing;

import org.ta4j.core.num.Num;

public interface SwingPoint {

    boolean unstable();

    default boolean stable() {
        return !unstable();
    }

    int index();

    Num candleValue();
}
