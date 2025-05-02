package com.becker.freelance.app;

import com.becker.freelance.backtest.util.PathUtil;
import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.strategies.BaseStrategy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class BacktestAppInitiatingUtil {
    
    private final PropertyAsker propertyAsker;
    
    public BacktestAppInitiatingUtil(){
        propertyAsker = new PropertyAsker();
    }

    public void saveProperties(BaseStrategy baseStrategy, Integer numberOfThreads, List<Pair> pairs, AppMode appMode) {
        Path propertiesPath = getPropertiesPath();

        List<String> content = List.of(
                baseStrategy.getName(),
                String.valueOf(numberOfThreads),
                pairs.stream().map(Pair::shortName).collect(Collectors.joining(";")),
                appMode.getDescription()
        );

        try {
            Files.writeString(propertiesPath, String.join("\n", content));
        } catch (IOException e) {
            throw new IllegalStateException("Could not write Properties to file " + propertiesPath, e);
        }
    }

    public Optional<LastExecutionProperties> findProperties() {
        Path path = getPropertiesPath();

        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read file " + path, e);
        }

        if (lines.size() != 4) {
            return Optional.empty();
        }

        BaseStrategy baseStrategy = BaseStrategy.loadAll().stream().filter(strategy -> strategy.getName().equals(lines.get(0))).findAny().orElseThrow(IllegalStateException::new);
        Integer numberOfThreads = Integer.parseInt(lines.get(1));
        Set<String> savedPairs = Set.of(lines.get(2).split(";"));
        List<Pair> pairs = Pair.allPairs().stream().filter(pair -> savedPairs.contains(pair.shortName())).toList();
        AppMode appMode = AppMode.findAll().stream().filter(mode -> mode.getDescription().equals(lines.get(3))).findAny().orElseThrow(IllegalStateException::new);

        propertyAsker.log(List.of(
                "\t\tStrategie:",
                "\t\t\t" + baseStrategy.getName(),
                "\t\tAnzahl an Threads:",
                "\t\t\t" + numberOfThreads,
                "\t\tPairs:",
                "\t\t\t" + pairs.stream().map(Pair::technicalName).toList(),
                "\t\tAppMode:",
                "\t\t\t" + appMode.getDescription()
        ));

        YesNoAnswers yesNoAnswers = propertyAsker.askProperty(List.of(YesNoAnswers.values()), YesNoAnswers::toString, "Auswahl (Soll mit diesen Parametern gestartet werden?)");

        if (yesNoAnswers == YesNoAnswers.NO) {
            return Optional.empty();
        }

        return Optional.of(
                new LastExecutionProperties(baseStrategy, numberOfThreads, pairs, appMode)
        );
    }

    private Path getPropertiesPath() {
        Path path = Path.of(PathUtil.fromRelativePath("backtest-app\\config\\last-execution.csv"));

        try {

            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            if (!Files.exists(path)) {
                return Files.createFile(path);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not create file " + path, e);
        }

        return path;
    }


    public BaseStrategy askStrategy() {
        BaseStrategy strategy = propertyAsker.askProperty(BaseStrategy.loadAll(), BaseStrategy::getName, "Strategie");
        return strategy;
    }


    public Integer askNumberOfThreads() {
        Integer numThreads = propertyAsker.askProperty(List.of(1, 20, 40, 80), i -> Integer.toString(i), "Anzahl an Threads");
        return numThreads;
    }

    public List<Pair> askPair(AppMode appMode) {
        List<Pair> pairs = Pair.allPairs().stream().filter(pair -> pair.isExecutableInAppMode(appMode)).distinct().toList();
        List<Pair> pair = propertyAsker.askMultipleProperty(pairs, Pair::technicalName, "Pair (Oder für mehrere gleichzeitig mit Komma getrennt)");
        return pair;
    }

    public AppMode askAppMode() {
        AppMode appMode = propertyAsker.askProperty(AppMode.findAll(), AppMode::getDescription, "AppMode");
        appMode.onSelection();
        return appMode;
    }

    private static enum YesNoAnswers {
        YES, NO
    }

    public record LastExecutionProperties(BaseStrategy baseStrategy, Integer numberOfThread, List<Pair> pairs,
                                          AppMode appMode) {
    }

}
