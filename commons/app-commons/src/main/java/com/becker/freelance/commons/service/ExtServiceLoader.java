package com.becker.freelance.commons.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public class ExtServiceLoader {

    private static final Map<Class<?>, Object> singletons = new HashMap<>();

    private ExtServiceLoader() {
    }

    public static <T> Stream<T> loadMultiple(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .stream()
                .map(ServiceLoader.Provider::get);
    }

    public static <T> T loadSingle(Class<T> clazz) {
        List<ServiceLoader.Provider<T>> providers = ServiceLoader.load(clazz).stream().toList();

        if (providers.size() > 1) {
            throw new IllegalStateException("Found multiple Instances for " + clazz.getName() + ": " + providers);
        }
        if (providers.isEmpty()) {
            throw new IllegalArgumentException("Could not find Instance for " + clazz.getName());
        }

        return providers.get(0).get();
    }

    public static <T> T loadSingleton(Class<T> clazz) {
        singletons.computeIfAbsent(clazz, ExtServiceLoader::loadSingleton);
        return clazz.cast(singletons.get(clazz));
    }
}
