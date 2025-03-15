package com.becker.freelance.indicators.ta.stochasticrsi;

import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

public record RsiResult(Num percentK, Num percentD) {

    public boolean isKGreaterD() {
        return percentK().isGreaterThan(percentD());
    }

    public boolean isDGreaterK() {
        return percentD().isGreaterThan(percentK());
    }

    public Num midValue() {
        return percentK().plus(percentD()).dividedBy(DecimalNum.valueOf(2));
    }

    public boolean isMidGreaterThan(Double other) {
        return midValue().isGreaterThan(DecimalNum.valueOf(other));
    }

    public boolean isMidLessThan(Double other) {
        return midValue().isLessThan(DecimalNum.valueOf(other));
    }
}
