package com.jamub.payaccess.api.models.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountBalanceResponse {
    private Double accountBalance;
    private Double floatingBalance;

    public AccountBalanceResponse(Double accountBalance, Double floatingBalance) {
        this.accountBalance = accountBalance;
        this.floatingBalance = floatingBalance;
    }
}
