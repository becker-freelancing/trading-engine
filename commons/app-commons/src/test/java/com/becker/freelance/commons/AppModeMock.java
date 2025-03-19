package com.becker.freelance.commons;



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
