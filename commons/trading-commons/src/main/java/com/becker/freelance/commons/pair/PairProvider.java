package com.becker.freelance.commons.pair;

import java.util.List;
import java.util.ServiceLoader;

public interface PairProvider {

    static List<Pair> allPairs() {
        ServiceLoader<PairProvider> pairs = ServiceLoader.load(PairProvider.class);
        return pairs.stream().map(ServiceLoader.Provider::get).map(PairProvider::get).flatMap(List::stream).toList();
    }

    List<Pair> get();
}
