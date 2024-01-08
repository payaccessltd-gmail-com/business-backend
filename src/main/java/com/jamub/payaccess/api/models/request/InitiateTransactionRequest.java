package com.jamub.payaccess.api.models.request;


import com.jamub.payaccess.api.enums.Channel;
import com.jamub.payaccess.api.models.Card;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Getter
@Setter
public class InitiateTransactionRequest {

    @NotBlank(message = "Incomplete request parameters. Order reference number not provided")
    private String orderRef;
    @NotBlank(message = "Incomplete request parameters. Merchant code for the transaction not provided")
    private String merchantCode;
    @NotBlank(message = "Incomplete request parameters. Redirect URL for the merchant has not been provided")
    private String redirectUrl;
    @NotBlank(message = "Incomplete request parameters. Currency code for the transaction not provided")
    private String currencyCode;
    @NotNull(message = "Incomplete request parameters. Transaction amount not provided")
    @DecimalMin(value = "0.10", message = "Please Enter a valid Amount. Minimum amount acceptable is 0.10")
    private BigDecimal amount;
    @NotBlank(message = "Incomplete request parameters. Terminal code for the payment not provided")
    private String terminalCode;
    @NotBlank(message = "Incomplete request parameters. Channel of payment not specified")
    @Pattern(regexp = "WEB|POS|ONLINE_BANKING|MOBILE|WALLET|ATM|NOT_SPECIFIED|SYSTEM|USSD|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: WEB, POS, ONLINE_BANKING, MOBILE, WALLET, ATM, SYSTEM, USSD")
    private String channel;
    @NotNull(message = "Incomplete request parameters. Payment card details not provided")
    private Card cardDetails;
    @NotBlank(message = "Incomplete request parameters. Customer identification not provided")
    private String customerId;
    private String customData;

}
