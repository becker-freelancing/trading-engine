package com.becker.freelance.backtest.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    public static void createIfNotExists(Path path) throws IOException {
        if (Files.exists(path)) {
            return;
        }

        Files.createDirectories(path.getParent());
        Files.createFile(path);
    }
}
