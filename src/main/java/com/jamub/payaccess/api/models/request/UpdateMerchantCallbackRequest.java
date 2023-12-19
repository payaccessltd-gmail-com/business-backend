package com.jamub.payaccess.api.models.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateMerchantCallbackRequest {

    @NotBlank(message = "Incomplete request parameters. Web hook URL not provided")
    private String webhookUrl;

    @NotBlank(message = "Incomplete request parameters. Callback URL not provided")
    private String callbackUrl;

    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;
}
