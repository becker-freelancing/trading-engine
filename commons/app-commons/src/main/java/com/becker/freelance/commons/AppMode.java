package com.becker.freelance.commons;

import java.util.List;
import java.util.ServiceLoader;

public interface AppMode {

    public static List<AppMode> findAll(){
        ServiceLoader<AppMode> appModes = ServiceLoader.load(AppMode.class);
        return appModes.stream().map(ServiceLoader.Provider::get).toList();
    }

    public static AppMode fromDescription(String description){
        return findAll().stream()
                .filter(mode -> description.equals(mode.getDescription()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Could not find App Mode with description " + description));
    }

    public boolean isDemo();

    public String getDataSourceName();

    public default String getDescription(){
        return getDataSourceName() + "_" + (isDemo() ? "DEMO" : "LIVE");
    }

}
