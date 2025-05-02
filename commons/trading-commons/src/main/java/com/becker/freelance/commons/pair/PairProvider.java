package com.becker.freelance.commons.pair;

import com.becker.freelance.commons.service.ExtServiceLoader;

import java.util.List;

public interface PairProvider {

    static List<Pair> allPairs() {
        return ExtServiceLoader.loadMultiple(PairProvider.class)
                .map(PairProvider::get)
                .flatMap(List::stream).toList();
    }

    List<Pair> get();
}
