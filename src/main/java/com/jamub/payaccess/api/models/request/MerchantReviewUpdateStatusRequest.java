package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.MerchantReviewStatus;
import com.jamub.payaccess.api.enums.MerchantStage;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class MerchantReviewUpdateStatusRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Merchant identification not provided")
    private String merchantCode;

    @NotNull(message = "Incomplete request parameters. Stage of approval not provided")
    @Pattern(regexp = "MERCHANT_KYC|MERCHANT_ABOUT_BUSINESS|MERCHANT_BIO_DATA|MERCHANT_BUSINESS_DATA|MERCHANT_BUSINESS_ACCOUNT_DATA|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: MERCHANT_KYC, MERCHANT_ABOUT_BUSINESS, MERCHANT_BIO_DATA, MERCHANT_BUSINESS_DATA, MERCHANT_BUSINESS_ACCOUNT_DATA")
    private String merchantStage;

    @NotNull(message = "Incomplete request parameters. Merchant review status not provided")
    @Pattern(regexp = "REJECTED|REQUEST_UPDATE|APPROVED|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: REJECTED, REQUEST_UPDATE, APPROVED")
    private String merchantReviewStatus;

    @NotBlank(message = "Incomplete request parameters. Reason key not provided")
    private String reason;

}
