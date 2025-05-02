package com.becker.freelance.commons;


import com.becker.freelance.commons.app.AppMode;

public class AppModeMock2 implements AppMode {
    @Override
    public boolean isDemo() {
        return false;
    }

    @Override
    public String getDataSourceName() {
        return "MOCK2";
    }

    @Override
    public boolean isEqual(AppMode other) {
        return false;
    }

}
