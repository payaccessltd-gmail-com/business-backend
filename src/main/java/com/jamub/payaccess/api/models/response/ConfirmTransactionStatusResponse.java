package com.jamub.payaccess.api.models.response;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ConfirmTransactionStatusResponse {

    private String responseCode;
    private String responseDescription;
    private String transactionReference;
    private String channelTransactionReference;
    private BigDecimal amount;
    private BigDecimal remittanceAmount;
    private String customerName;
    private String bank;
}
