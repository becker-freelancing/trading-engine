package com.becker.freelance.trading.external.services.fees;

import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.trading.external.services.registry.ExternalService;

import java.time.LocalDateTime;

public interface TradingFeeCalculator extends ExternalService {


    public Decimal calculateOpenFee(Decimal positionSize, TimeSeriesEntry openPrice);

    public Decimal calculateOpenFee(Position position);

    public Decimal calculateCloseFee(Decimal positionSize, TimeSeriesEntry closePrice);

    public Decimal calculateCloseFee(Position position, TimeSeriesEntry closePrice);

    public Decimal calculateOtherFees(Position position, LocalDateTime closeTime);

    public default Decimal calculateTotalFees(Position position, TimeSeriesEntry closePrice) {
        return calculateOpenFee(position)
                .add(calculateCloseFee(position, closePrice))
                .add(calculateOtherFees(position, closePrice.time()));
    }
}
