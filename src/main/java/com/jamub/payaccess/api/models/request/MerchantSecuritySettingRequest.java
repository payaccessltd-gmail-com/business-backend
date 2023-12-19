package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MerchantSecuritySettingRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Two-factor authentication for payment and transfer not provided")
    private String twoFactorAuthForPaymentAndTransfer;

    @NotBlank(message = "Incomplete request parameters. Two-factor authentication for login key not provided")
    private String twoFactorAuthForLogin;

    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;
}
