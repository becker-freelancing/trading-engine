package com.becker.freelance.capital.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateSessionResponse {

    private AccountInfo accountInfo;
    private AccountType accountType;
    private List<AccountItem> accounts;
    private String clientId;
    private String currencyIsoCode;
    private String currencySymbol;
    private String currentAccountId;
    private boolean hasActiveDemoAccounts;
    private boolean hasActiveLiveAccounts;
    private String streamingHost;
    private int timezoneOffset;
    private Boolean trailingStopsEnabled;

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public List<AccountItem> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountItem> accounts) {
        this.accounts = accounts;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCurrencyIsoCode() {
        return currencyIsoCode;
    }

    public void setCurrencyIsoCode(String currencyIsoCode) {
        this.currencyIsoCode = currencyIsoCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrentAccountId() {
        return currentAccountId;
    }

    public void setCurrentAccountId(String currentAccountId) {
        this.currentAccountId = currentAccountId;
    }

    public boolean isHasActiveDemoAccounts() {
        return hasActiveDemoAccounts;
    }

    public void setHasActiveDemoAccounts(boolean hasActiveDemoAccounts) {
        this.hasActiveDemoAccounts = hasActiveDemoAccounts;
    }

    public boolean isHasActiveLiveAccounts() {
        return hasActiveLiveAccounts;
    }

    public void setHasActiveLiveAccounts(boolean hasActiveLiveAccounts) {
        this.hasActiveLiveAccounts = hasActiveLiveAccounts;
    }

    public String getStreamingHost() {
        return streamingHost;
    }

    public void setStreamingHost(String streamingHost) {
        this.streamingHost = streamingHost;
    }

    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public Boolean getTrailingStopsEnabled() {
        return trailingStopsEnabled;
    }

    public void setTrailingStopsEnabled(Boolean trailingStopsEnabled) {
        this.trailingStopsEnabled = trailingStopsEnabled;
    }
}
