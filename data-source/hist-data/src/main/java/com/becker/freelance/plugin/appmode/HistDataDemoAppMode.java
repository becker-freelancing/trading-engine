package com.becker.freelance.plugin.appmode;

import com.becker.freelance.commons.AppMode;

public class HistDataDemoAppMode implements AppMode {
    @Override
    public boolean isDemo() {
        return true;
    }

    @Override
    public String getDataSourceName() {
        return "HISTDATA";
    }
}
