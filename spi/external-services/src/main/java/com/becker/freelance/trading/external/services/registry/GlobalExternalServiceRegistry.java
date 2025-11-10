package com.becker.freelance.trading.external.services.registry;

import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

class GlobalExternalServiceRegistry implements ExternalServiceRegistry {


    private static final Set<? extends ExternalServiceBuilder> REGISTERED_BUILDERS;

    static {
        REGISTERED_BUILDERS = ServiceLoader.load(ExternalServiceBuilder.class).stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toSet());
    }

    @Override
    public <B extends ExternalServiceBuilder> B requireServiceBuilder(Class<B> builderClass) {
        List<? extends ExternalServiceBuilder> builders = REGISTERED_BUILDERS.stream()
                .filter(builder -> builderClass.isAssignableFrom(builder.getClass()))
                .toList();
        if (builders.size() != 1) {
            throw new IllegalStateException("Found " + builders.size() + " builders, for type " + builderClass + ", where 1 was expected: " + builders);
        }
        return builderClass.cast(builders.get(0));
    }

    @Override
    public <B extends SupportableExternalServiceBuilder<?, SP, ?>, SP> B requireSupportsServiceBuilder(Class<B> builderClass, SP supportsParam) {
        return REGISTERED_BUILDERS.stream().filter(builder -> builderClass.isAssignableFrom(builder.getClass()))
                .map(builder -> (SupportableExternalServiceBuilder) builder)
                .filter(builder -> builder.supports(supportsParam))
                .findAny()
                .map(builderClass::cast)
                .orElseThrow(() -> new IllegalStateException("Could not find supportable service builder of type " + builderClass));
    }

    @Override
    public <B extends ExternalServiceBuilder> List<B> requireServiceBuilders(Class<B> builderClass) {
        return REGISTERED_BUILDERS.stream()
                .filter(builder -> builderClass.isAssignableFrom(builder.getClass()))
                .map(builderClass::cast)
                .toList();
    }

    @Override
    public ScopedExternalServiceRegistry newScopedExternalServiceRegistry() {
        return new ScopedExternalServiceRegistryImpl(this);
    }
}
