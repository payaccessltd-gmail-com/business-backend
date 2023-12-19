package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestAttribute;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MerchantTransactionFeePayerRequest {

    @NotNull(message = "Incomplete request parameters. Specify if the merchant must pay transaction fee")
    Boolean merchantMustPayTransactionFee;

    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    Long merchantId;
}
