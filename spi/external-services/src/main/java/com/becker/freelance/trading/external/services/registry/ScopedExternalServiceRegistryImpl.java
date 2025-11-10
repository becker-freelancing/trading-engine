package com.becker.freelance.trading.external.services.registry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ScopedExternalServiceRegistryImpl implements ScopedExternalServiceRegistry {

    private final ExternalServiceRegistry delegate;
    private final Set<ExternalService> scopedServices;

    public ScopedExternalServiceRegistryImpl(ExternalServiceRegistry delegate) {
        this.delegate = delegate;
        this.scopedServices = new HashSet<>();
    }

    @Override
    public <S extends ExternalService> void registerScopedExternalService(S service) {
        scopedServices.add(service);
    }

    @Override
    public <S extends ExternalService> S requireScopedExternalService(Class<S> serviceClass) {
        List<ExternalService> services = scopedServices.stream()
                .filter(entry -> serviceClass.isAssignableFrom(entry.getClass()))
                .toList();

        if (services.size() != 1) {
            throw new IllegalStateException("Found " + services.size() + "scoped services, for type " + serviceClass + ", where 1 was expected: " + services);
        }
        return serviceClass.cast(services.get(0));
    }

    @Override
    public <B extends ExternalServiceBuilder> B requireServiceBuilder(Class<B> builderClass) {
        return delegate.requireServiceBuilder(builderClass);
    }

    @Override
    public <B extends SupportableExternalServiceBuilder<?, SP, ?>, SP> B requireSupportsServiceBuilder(Class<B> builderClass, SP supportsParam) {
        return delegate.requireSupportsServiceBuilder(builderClass, supportsParam);
    }

    @Override
    public <B extends ExternalServiceBuilder> List<B> requireServiceBuilders(Class<B> builderClass) {
        return delegate.requireServiceBuilders(builderClass);
    }

    @Override
    public ScopedExternalServiceRegistry newScopedExternalServiceRegistry() {
        return this;
    }
}
