package com.becker.freelance.trading.external.services.registry;

public interface ScopedExternalServiceRegistry extends ExternalServiceRegistry {

    public <S extends ExternalService> void registerScopedExternalService(S service);

    public <S extends ExternalService> S requireScopedExternalService(Class<S> serviceClass);
}
