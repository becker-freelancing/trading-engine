package com.becker.freelance.commons;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtil {

    public static Path getBasePath(){
        String appDataPath = System.getenv("APPDATA");

        if (appDataPath == null || appDataPath.isEmpty()) {
            String userHome = System.getProperty("user.home");
            appDataPath = Paths.get(userHome, ".config").toString();
        }

        return Path.of(appDataPath, "krypto-java");
    }

    public static String fromRelativePath(String path){
        return getBasePath() + FileSystems.getDefault().getSeparator() + path;
    }

    public static String rootResultDir(){
        return fromRelativePath("results\\");
    }

    public static String resultDirForStrategy(String strategyName){
        return rootResultDir() + strategyName + "\\";
    }

    public static String fromModelsDir(String name) {
        return fromRelativePath("models\\" + name);
    }
}
