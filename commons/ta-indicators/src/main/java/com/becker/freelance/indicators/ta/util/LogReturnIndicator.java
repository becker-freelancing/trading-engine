package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

public class LogReturnIndicator implements Indicator<Num> {

    private final Indicator<Num> closePrice;

    public LogReturnIndicator(Indicator<Num> closePrice) {
        this.closePrice = closePrice;
    }

    @Override
    public Num getValue(int index) {
        if (index == 0) {
            return DecimalNum.ZERO;
        }
        double v = closePrice.getValue(index).doubleValue();
        double v1 = closePrice.getValue(index - 1).doubleValue();
        double log = Math.log(v / v1);
        DecimalNum decimalNum = DecimalNum.valueOf(log);
        return decimalNum;
//        return closePrice.getValue(index).dividedBy(closePrice.getValue(index - 1)).log();
    }

    @Override
    public int getUnstableBars() {
        return closePrice.getUnstableBars() + 1;
    }

    @Override
    public BarSeries getBarSeries() {
        return closePrice.getBarSeries();
    }
}

