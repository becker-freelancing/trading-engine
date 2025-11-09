package com.becker.freelance.app;

class AbstractLocalBacktestContinueApp {

//    private final BacktestAppInitiatingUtil appInitiatingUtil;
//    private Decimal initialWalletAmount;
//    private LocalDateTime fromTime;
//    private LocalDateTime toTime;
//    private AppMode appMode;
//    private List<Pair> pairs;
//
//    AbstractLocalBacktestContinueApp() {
//        this.appInitiatingUtil = new BacktestAppInitiatingUtil();
//    }
//
//    public LocalBacktestPort build() {
//        PropertyAsker propertyAsker = new PropertyAsker();
//        StrategyCreator strategy = appInitiatingUtil.askStrategy();
//        List<Path> strategyResults;
//        try (Stream<Path> walk = Files.walk(Path.of(PathUtil.resultDirForStrategy(strategy.strategyName())))) {
//            strategyResults = walk.filter(Files::isRegularFile)
//                    .filter(path -> path.toString().endsWith(".zst")).sorted(Comparator.comparing(path -> path.getFileName().toString())).toList();
//        } catch (IOException e) {
//            throw new IllegalStateException("Could not read results for strategy " + strategy.strategyName(), e);
//        }
//        Path resultPath = propertyAsker.askProperty(strategyResults, path -> path.getFileName().toString(), "Ergebnis");
//        Integer numThreads = appInitiatingUtil.askNumberOfThreads();
//        Set<BacktestResultContent> backtestResultContents = new BacktestResultReader(resultPath).readCsvContent();
//        parseAppParameter(backtestResultContents);
//        Path resultWriteFile = unzipResultFile(resultPath);
//        Set<StrategyCreationParameter> parameters = backtestResultContents.stream()
//                .map(BacktestResultContent::parameters)
//                .map(this::map)
//                .collect(Collectors.toSet());
//
//
//        return new LocalBacktestContinueInteractor(
//                initialWalletAmount,
//                fromTime,
//                toTime,
//                strategy,
//                appMode,
//                pairs,
//                numThreads,
//                parameters,
//                resultWriteFile,
//                BacktestMode.valueOf(backtestResultContents.stream().findAny().orElseThrow().getBacktestMode())
//        );
//    }
//
//    private StrategyCreationParameter map(Map<String, Decimal> stringDecimalMap) {
//        return new DefaultStrategyCreationParameter(stringDecimalMap.entrySet().stream()
//                .collect(Collectors.toMap(
//                        entry -> new StringParameterName(entry.getKey()),
//                        Map.Entry::getValue
//                )));
//    }
//
//    private Path unzipResultFile(Path resultPath) {
//        return new BacktestResultZipper(resultPath).unzipFile();
//    }
//
//    private void parseAppParameter(Set<BacktestResultContent> backtestResultContents) {
//        BacktestResultContent result = backtestResultContents.stream().findAny().orElseThrow(() -> new IllegalStateException("No Results found"));
//
//        fromTime = result.fromTime();
//        toTime = result.toTime();
//        initialWalletAmount = result.initialWalletAmount();
//        pairs = result.parsePairs();
//        appMode = AppMode.fromDescription(result.appMode());
//    }
}
