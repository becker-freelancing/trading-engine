package com.becker.freelance.indicators.ta.supportresistence;

import com.becker.freelance.indicators.ta.swing.SwingPoint;
import org.ta4j.core.num.Num;

import java.util.List;

public interface Zone<T extends SwingPoint> {

    int start();

    int end();

    Num lowerLevel();

    Num upperLevel();

    Num score();

    int numberOfHits();

    List<T> hits();
}
