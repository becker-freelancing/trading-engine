package com.becker.freelance.capital.env;

public class EnvironmentProvider {


    public CapitalUserConfig userConfig() {
        return new CapitalUserConfig(login(), password(), apiKey());
    }

    public String baseURL() {
        if (System.getenv("APPMODE").contains("DEMO")) {
            return demoURL();
        }
        return prodURL();
    }

    String demoURL() {
        return System.getenv("CAPITAL_DEMO_URL");
    }


    String prodURL() {
        return System.getenv("CAPITAL_PROD_URL");
    }


    String login() {
        return System.getenv("CAPITAL_LOGIN");
    }


    String password() {
        return System.getenv("CAPITAL_PASSWORD");
    }


    String apiKey() {
        return System.getenv("CAPITAL_API_KEY");
    }
}
