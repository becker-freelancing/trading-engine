package com.becker.freelance.bybit.broker;

import com.becker.freelance.bybit.env.BybitEnvironmentProvider;
import com.becker.freelance.math.Decimal;
import com.bybit.api.client.domain.account.AccountType;
import com.bybit.api.client.domain.account.request.AccountDataRequest;
import com.bybit.api.client.restApi.BybitApiAccountRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class BrokerApiClient {

    private final static Logger logger = LoggerFactory.getLogger(BrokerApiClient.class);

    private final BybitApiAccountRestClient accountRestClient;

    public BrokerApiClient() {
        BybitEnvironmentProvider bybitEnvironmentProvider = new BybitEnvironmentProvider();
        BybitApiClientFactory bybitApiClientFactory = BybitApiClientFactory.newInstance(bybitEnvironmentProvider.apiKey(), bybitEnvironmentProvider.secret(), bybitEnvironmentProvider.baseURL());
        this.accountRestClient = bybitApiClientFactory.newAccountRestClient();
    }

    public Decimal getAmount() {
        Map<String, Object> response = (Map<String, Object>) accountRestClient.getWalletBalance(AccountDataRequest.builder()
                .accountType(AccountType.UNIFIED).build());

        if (0 != (int) response.get("retCode")) {
            logger.error("Could not request wallet amount. Error-Code: {}, Error-Message: {}", response.get("retCode"), response.get("retMsg"));
        }

        Map<String, Object> responseResult = (Map<String, Object>) response.get("result");
        List<Map<String, String>> list = (List<Map<String, String>>) responseResult.get("list");

        return new Decimal(list.get(0).get("totalEquity"));
    }

    public Decimal getMargin() {
        Map<String, Object> response = (Map<String, Object>) accountRestClient.getWalletBalance(AccountDataRequest.builder()
                .accountType(AccountType.UNIFIED).build());

        if (0 != (int) response.get("retCode")) {
            logger.error("Could not request wallet amount. Error-Code: {}, Error-Message: {}", response.get("retCode"), response.get("retMsg"));
        }

        Map<String, Object> responseResult = (Map<String, Object>) response.get("result");
        List<Map<String, String>> list = (List<Map<String, String>>) responseResult.get("list");

        return new Decimal(list.get(0).get("totalInitialMargin"));
    }
}
