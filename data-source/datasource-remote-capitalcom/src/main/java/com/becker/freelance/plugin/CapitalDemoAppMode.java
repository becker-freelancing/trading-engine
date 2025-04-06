package com.becker.freelance.plugin;

import com.becker.freelance.commons.AppMode;

public class CapitalDemoAppMode implements AppMode {


    @Override
    public boolean isDemo() {
        return true;
    }

    @Override
    public String getDataSourceName() {
        return "CAPITAL";
    }

    @Override
    public boolean isEqual(AppMode other) {
        return getDataSourceName().equals(other.getDataSourceName()) && isDemo() == other.isDemo();
    }

    @Override
    public void onSelection() {
        throw new IllegalStateException("System Env must be set");
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AppMode that = (AppMode) object;
        return isEqual(that);
    }

}
