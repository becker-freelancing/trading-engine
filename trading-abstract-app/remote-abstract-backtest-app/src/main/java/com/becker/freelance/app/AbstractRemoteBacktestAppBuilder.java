package com.becker.freelance.app;

public class AbstractRemoteBacktestAppBuilder {

    public Runnable build() {
        return new AbstractRemoteBacktestApp();
    }
}
