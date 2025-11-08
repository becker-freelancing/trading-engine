package com.becker.freelance.trading.external.services.registry;

public interface ParamsExternalServiceBuilder<PARAMS, SERVICE extends ExternalService> extends ExternalServiceBuilder {

    public SERVICE build(PARAMS params);
}
