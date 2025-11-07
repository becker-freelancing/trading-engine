package com.becker.freelance.trading.external.services.registry;

public interface NoParamsExternalServiceBuilder<SERVICE extends ExternalService> extends ExternalServiceBuilder<SERVICE> {

    public SERVICE build();
}
