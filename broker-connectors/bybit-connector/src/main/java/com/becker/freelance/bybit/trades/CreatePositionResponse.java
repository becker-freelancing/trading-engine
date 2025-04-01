package com.becker.freelance.bybit.trades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
class CreatePositionResponse {

    private String dealReference;
}
