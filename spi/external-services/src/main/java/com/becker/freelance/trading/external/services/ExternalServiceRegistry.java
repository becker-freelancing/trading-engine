package com.becker.freelance.trading.external.services;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

public class ExternalServiceRegistry {

    private static final Set<? extends ExternalServiceBuilder> REGISTERED_BUILDERS;

    static {
        REGISTERED_BUILDERS = ServiceLoader.load(ExternalServiceBuilder.class).stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toSet());
    }

    public <B extends ExternalServiceBuilder> Optional<B> findServiceBuilder(Class<B> builderClass) {
        return REGISTERED_BUILDERS.stream().filter(builder -> builderClass.equals(builder.getClass()))
                .findAny()
                .map(builderClass::cast);
    }

    public <S extends ExternalService> Optional<S> findService(Class<S> serviceClass, Object params) {
        return REGISTERED_BUILDERS.stream().filter(builder -> serviceClass.equals(builder.getServiceClass()))
                .findAny()
                .map(builder -> builder.build(params))
                .map(serviceClass::cast);
    }
}
