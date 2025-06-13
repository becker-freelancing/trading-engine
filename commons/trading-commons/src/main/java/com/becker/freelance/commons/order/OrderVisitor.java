package com.becker.freelance.commons.order;

public interface OrderVisitor {

    public void accept(ConditionalOrder conditionalOrder);

    public void accept(LimitOrder limitOrder);

    public void accept(MarketOrder marketOrder);
}
