package com.jamub.payaccess.api.models.request;


import com.jamub.payaccess.api.enums.PayAccessCurrency;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantPaymentSettingRequest {
    private PayAccessCurrency defaultCurrency;
    private Boolean enableAcceptPOSChannel;
    private Boolean enableAcceptBankTransfers;
    private Boolean enableAcceptCardPayment;
    private Boolean enableAcceptMobileMoneyTransfer;
    private Boolean enableUSSDTransfer;
    private Long merchantId;
}
