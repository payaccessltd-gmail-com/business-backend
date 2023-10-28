package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantSecuritySettingRequest extends BaseRequest{
    private String twoFactorAuthForPaymentAndTransfer;
    private String twoFactorAuthForLogin;
    private String merchantId;
}
