package com.becker.freelance.trading.external.services;

public interface ExternalServiceBuilder<PARAMS, SERVICE extends ExternalService> {

    public SERVICE build(PARAMS params);

    public Class<SERVICE> getServiceClass();
}
