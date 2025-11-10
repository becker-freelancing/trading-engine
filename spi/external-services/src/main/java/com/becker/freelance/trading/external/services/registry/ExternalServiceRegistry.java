package com.becker.freelance.trading.external.services.registry;

import java.util.List;

public interface ExternalServiceRegistry {


    public static ExternalServiceRegistry globalServiceRegistry() {
        return new GlobalExternalServiceRegistry();
    }

    public <B extends ExternalServiceBuilder> B requireServiceBuilder(Class<B> builderClass);

    public <B extends SupportableExternalServiceBuilder<?, SP, ?>, SP> B requireSupportsServiceBuilder(Class<B> builderClass, SP supportsParam);

    public <B extends ExternalServiceBuilder> List<B> requireServiceBuilders(Class<B> builderClass);

    public ScopedExternalServiceRegistry newScopedExternalServiceRegistry();
}
