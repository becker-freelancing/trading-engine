package com.becker.freelance.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

class PropertyAsker {

    private static final Logger logger = LoggerFactory.getLogger(PropertyAsker.class);


    private static <T> T askInput(List<T> selections, String name, String message) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                logger.info(message, name);
                String input = scanner.nextLine();
                return parseSingleInput(selections, input);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private static <T> T parseSingleInput(List<T> selections, String input) {
        int choice = Integer.parseInt(input);
        if (choice >= 1 && choice <= selections.size()) {
            return selections.get(choice - 1);
        } else {
            return selections.get(selections.size() - 1);
        }
    }

    private static <T> List<T> askMultipleInput(List<T> selections, String name, String message) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                logger.info(message, name);
                String input = scanner.nextLine();
                return parseMultipleInput(selections, input);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private static <T> List<T> parseMultipleInput(List<T> selections, String input) {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .filter(idx -> idx >= 1 && idx <= selections.size())
                .map(idx -> selections.get(idx - 1))
                .toList();
    }

    private static <T> void printSelection(List<T> selections, Function<T, String> toStringConverter, String name) {
        logger.info("\t\t===============================================================================");
        logger.info("\t\t================== Wähle eine {} für den Backtest ==================", name);
        logger.info("\t\t===============================================================================");

        for (int i = 1; i <= selections.size(); i++) {
            T selection = selections.get(i - 1);
            logger.info("\t\t{}.\t{}", i, toStringConverter.apply(selection));
        }
    }

    public <T> T askProperty(List<T> selections, Function<T, String> toStringConverter, String name){
        printSelection(selections, toStringConverter, name);
        String message = "\t\tGeben Sie die Nummer der gewünschten {} ein: ";
        return askInput(selections, name, message);
    }

    public <T> List<T> askMultipleProperty(List<T> selections, Function<T, String> toStringConverter, String name) {
        printSelection(selections, toStringConverter, name);
        String message = "\t\tGeben Sie die Nummern der gewünschten {} ein (Komma separiert, falls mehrere): ";
        return askMultipleInput(selections, name, message);
    }
}
