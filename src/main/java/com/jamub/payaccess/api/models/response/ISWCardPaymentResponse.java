package com.jamub.payaccess.api.models.response;

import com.jamub.payaccess.api.models.ISWError;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ISWCardPaymentResponse {
    private String transactionRef;
    private String paymentId;
    private String message;
    private String amount;
    private String responseCode;
    private String plainTextSupportMessage;
    private List<ISWError> errors;
}
