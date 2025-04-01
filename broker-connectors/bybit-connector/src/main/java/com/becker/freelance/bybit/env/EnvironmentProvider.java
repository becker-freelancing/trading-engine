package com.becker.freelance.bybit.env;


public class EnvironmentProvider {


    public String baseURL() {
        if (isDemo()) {
            return demoURL();
        }
        return prodURL();
    }

    public boolean isDemo() {
        return System.getenv("APPMODE").contains("DEMO");
    }

    String demoURL() {
        return System.getenv("BYBIT_DEMO_URL");
    }


    String prodURL() {
        return System.getenv("BYBIT_PROD_URL");
    }


    public String secret() {
        return System.getenv("BYBIT_SECRET");
    }


    public String apiKey() {
        return System.getenv("BYBIT_API_KEY");
    }
}
