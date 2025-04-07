package com.becker.freelance.indicators.ta.supportresistence;

import com.becker.freelance.indicators.ta.swing.SwingLowPoint;
import org.ta4j.core.num.Num;

import java.util.List;


public record Support(int start, int end, Num lowerLevel, Num upperLevel, Num score, int numberOfHits,
                      List<SwingLowPoint> hits) implements Zone<SwingLowPoint> {
}
