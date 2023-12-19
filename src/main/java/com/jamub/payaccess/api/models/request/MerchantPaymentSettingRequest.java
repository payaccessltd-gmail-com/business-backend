package com.jamub.payaccess.api.models.request;


import com.jamub.payaccess.api.enums.PayAccessCurrency;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class MerchantPaymentSettingRequest {

    @NotBlank(message = "Incomplete request parameters. Default currency not provided")
    @Pattern(regexp = "NGN|USD|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: NGN, USD")
    private String defaultCurrency;

    @NotNull(message = "Incomplete request parameters. POS Payments acceptance field not provided")
    private Boolean enableAcceptPOSChannel;

    @NotNull(message = "Incomplete request parameters. Bank transfer acceptance field not provided")
    private Boolean enableAcceptBankTransfers;

    @NotNull(message = "Incomplete request parameters. Card payment acceptance field not provided")
    private Boolean enableAcceptCardPayment;

    @NotNull(message = "Incomplete request parameters. Mobile money transfer acceptance field not provided")
    private Boolean enableAcceptMobileMoneyTransfer;

    @NotNull(message = "Incomplete request parameters. USSD Transfer field not provided")
    private Boolean enableUSSDTransfer;

    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;
}
