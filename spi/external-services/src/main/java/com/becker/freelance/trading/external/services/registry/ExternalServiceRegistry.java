package com.becker.freelance.trading.external.services.registry;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

public class ExternalServiceRegistry {

    private static final Set<? extends NoParamsExternalServiceBuilder> NO_PARAMS_REGISTERED_BUILDERS;

    private static final Set<? extends ExternalServiceBuilder> REGISTERED_BUILDERS;
    private static final Set<? extends ParamsExternalServiceBuilder> PARAMS_REGISTERED_BUILDERS;

    static {
        REGISTERED_BUILDERS = ServiceLoader.load(ExternalServiceBuilder.class).stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toSet());
        NO_PARAMS_REGISTERED_BUILDERS = REGISTERED_BUILDERS.stream()
                .filter(builder -> NoParamsExternalServiceBuilder.class.isAssignableFrom(builder.getClass()))
                .map(builder -> (NoParamsExternalServiceBuilder) builder)
                .collect(Collectors.toSet());
        PARAMS_REGISTERED_BUILDERS = REGISTERED_BUILDERS.stream()
                .filter(builder -> ParamsExternalServiceBuilder.class.isAssignableFrom(builder.getClass()))
                .map(builder -> (ParamsExternalServiceBuilder) builder)
                .collect(Collectors.toSet());
    }

    public static ExternalServiceRegistry newInstance() {
        return new ExternalServiceRegistry();
    }

    public <B extends ExternalServiceBuilder> Optional<B> findServiceBuilder(Class<B> builderClass) {
        return REGISTERED_BUILDERS.stream().filter(builder -> builderClass.isAssignableFrom(builder.getClass()))
                .findAny()
                .map(builderClass::cast);
    }

    public <B extends ExternalServiceBuilder> B requireServiceBuilder(Class<B> builderClass) {
        return findServiceBuilder(builderClass).orElseThrow(() -> new IllegalStateException("Could not find service builder of type " + builderClass));
    }

    public <B extends SupportableExternalServiceBuilder<?, SP, ?>, SP> B requireSupportsServiceBuilder(Class<B> builderClass, SP supportsParam) {
        return REGISTERED_BUILDERS.stream().filter(builder -> builderClass.isAssignableFrom(builder.getClass()))
                .map(builder -> (SupportableExternalServiceBuilder) builder)
                .filter(builder -> builder.supports(supportsParam))
                .findAny()
                .map(builderClass::cast)
                .orElseThrow(() -> new IllegalStateException("Could not find supportable service builder of type " + builderClass));
    }

}
