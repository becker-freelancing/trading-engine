package com.becker.freelance.commons.position;


import com.becker.freelance.math.Decimal;

public interface TrailingPosition extends Position {

    public void setStopLevel(Decimal level);

    public Decimal initialStopLevel();
}
