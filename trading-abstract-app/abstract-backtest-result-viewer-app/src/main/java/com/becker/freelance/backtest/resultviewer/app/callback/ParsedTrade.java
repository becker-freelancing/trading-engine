package com.becker.freelance.backtest.resultviewer.app.callback;

import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public record ParsedTrade(LocalDateTime time, Decimal pnl) {
}
