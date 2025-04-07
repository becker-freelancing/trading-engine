package com.becker.freelance.indicators.ta.supportresistence;

import com.becker.freelance.indicators.ta.swing.SwingHighPoint;
import org.ta4j.core.num.Num;

import java.util.List;


public record Resistence(int start, int end, Num lowerLevel, Num upperLevel, Num score, int numberOfHits,
                         List<SwingHighPoint> hits) implements Zone<SwingHighPoint> {
}
