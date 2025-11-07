package com.becker.freelance.trading.external.services.broker;


import com.becker.freelance.trading.external.services.registry.ExternalService;

public interface AccountBalanceRequestor extends ExternalService {

    public ReadOnlyWallet getWallet();
}
