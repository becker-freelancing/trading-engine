package com.becker.freelance.trading.external.services.registry;

public interface ExternalServiceBuilder<SERVICE extends ExternalService> {

    public Class<? extends SERVICE> getServiceClass();
}
