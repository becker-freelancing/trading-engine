package com.becker.freelance.management.api;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.TradingFeeCalculator;
import com.becker.freelance.management.api.adaption.EntrySignalAdaptor;
import com.becker.freelance.management.api.adaption.EntrySignalAdaptorComposite;
import com.becker.freelance.management.api.environment.FileManagementEnvironmentProvider;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.CompositeStrategy;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.management.api.validation.EntrySignalValidatorComposite;
import com.becker.freelance.opentrades.AccountBalanceRequestor;
import com.becker.freelance.opentrades.BrokerSpecificsRequestor;
import com.becker.freelance.opentrades.ClosedTradesRequestor;
import com.becker.freelance.opentrades.OpenPositionRequestor;

import java.util.List;
import java.util.ServiceLoader;

public class ManagementLoader {

    public EntrySignalAdaptor findEntrySignalAdaptor() {

        return new EntrySignalAdaptorComposite(loadAll(EntrySignalAdaptor.class));
    }

    public EntrySignalValidator findEntrySignalValidator(CompositeStrategy compositeStrategy) {

        return new EntrySignalValidatorComposite(loadAll(EntrySignalValidator.class), compositeStrategy);
    }

    public ManagementEnvironmentProvider findEnvironmentProvider(AccountBalanceRequestor accountBalanceRequestor,
                                                                 BrokerSpecificsRequestor brokerSpecificsRequestor,
                                                                 OpenPositionRequestor openPositionRequestor,
                                                                 ClosedTradesRequestor closedTradesRequestor,
                                                                 EurUsdRequestor eurUsdRequestor,
                                                                 TradingFeeCalculator tradingFeeCalculator) {

        return new FileManagementEnvironmentProvider(accountBalanceRequestor,
                brokerSpecificsRequestor,
                openPositionRequestor,
                closedTradesRequestor,
                eurUsdRequestor,
                tradingFeeCalculator);
    }


    private <T> List<T> loadAll(Class<T> clazz) {
        return ServiceLoader.load(clazz).stream()
                .map(ServiceLoader.Provider::get)
                .toList();
    }
}
