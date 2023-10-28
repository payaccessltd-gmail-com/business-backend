package com.jamub.payaccess.api.models.request;


import com.jamub.payaccess.api.enums.Channel;
import com.jamub.payaccess.api.models.Card;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InitiateTransactionRequest {
    private String orderRef;
    private String merchantCode;
    private String redirectUrl;
    private String currencyCode;
    private BigDecimal amount;
    private String terminalCode;
    private Channel channel;
    private Card cardDetails;
    private String customerId;

}
