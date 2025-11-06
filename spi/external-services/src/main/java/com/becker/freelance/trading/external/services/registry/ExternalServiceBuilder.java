package com.becker.freelance.trading.external.services.registry;

public interface ExternalServiceBuilder<PARAMS, SERVICE extends ExternalService> {

    public SERVICE build(PARAMS params);

    public Class<? extends SERVICE> getServiceClass();
}
