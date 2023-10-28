package com.jamub.payaccess.api.models.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMerchantCallbackRequest {
    private String webhookUrl;
    private String callbackUrl;
    private Long merchantId;
}
