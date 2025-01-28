package com.becker.freelance.backtest.resultviewer.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

class PropertyAsker {

    private static final Logger logger = LoggerFactory.getLogger(PropertyAsker.class);


    public <T> T askProperty(List<T> selections, Function<T, String> toStringConverter, String name){
        logger.info("\t\t===============================================================================");
        logger.info("\t\t================== Wähle eine {} für die Ergebnisanalyse ==================", name);
        logger.info("\t\t===============================================================================");

        for (int i = 1; i <= selections.size(); i++){
            T selection = selections.get(i - 1);
            logger.info("\t\t{}.\t{}", i, toStringConverter.apply(selection));
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                logger.info("\t\tGeben Sie die Nummer der gewünschten {} ein: ", name);
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= selections.size()) {
                    return selections.get(choice - 1);
                } else {
                    return selections.get(selections.size() - 1);
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
