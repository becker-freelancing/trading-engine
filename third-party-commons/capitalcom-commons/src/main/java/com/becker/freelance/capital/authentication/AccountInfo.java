package com.becker.freelance.capital.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountInfo {
    private Float balance;
    private Float deposit;
    private Float profitLoss;
    private Float available;

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public Float getDeposit() {
        return deposit;
    }

    public void setDeposit(Float deposit) {
        this.deposit = deposit;
    }

    public Float getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(Float profitLoss) {
        this.profitLoss = profitLoss;
    }

    public Float getAvailable() {
        return available;
    }

    public void setAvailable(Float available) {
        this.available = available;
    }
}
