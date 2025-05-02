package com.becker.freelance.commons;


import com.becker.freelance.commons.app.AppMode;

public class AppModeMock implements AppMode {
    @Override
    public boolean isDemo() {
        return true;
    }

    @Override
    public String getDataSourceName() {
        return "MOCK";
    }

    @Override
    public boolean isEqual(AppMode other) {
        return false;
    }

}
