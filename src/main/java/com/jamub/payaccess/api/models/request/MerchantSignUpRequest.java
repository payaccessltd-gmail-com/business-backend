package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.BusinessCategory;
import com.jamub.payaccess.api.enums.BusinessType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class MerchantSignUpRequest extends BaseRequest{

    @NotNull(message = "Incomplete request parameters. Business category key not provided")
    @Pattern(regexp = "TRANSPORTATION|FARMING|FISHING|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: TRANSPORTATION, FARMING, FISHING")
    private String businessCategory;

    @NotNull(message = "Incomplete request parameters. Business type key not provided")
    @Pattern(regexp = "INDIVIDUAL|REGISTERED_BUSINESS|NGO_BUSINESS|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: INDIVIDUAL, REGISTERED_BUSINESS, NGO_BUSINESS")
    private String businessType;

    @NotNull(message = "Incomplete request parameters. Specify if you are a software developer")
    private Boolean softwareDeveloper;

    @NotBlank(message = "Incomplete request parameters. Merchant mobile number not provided")
    private String mobileNumber;

    @NotNull(message = "Incomplete request parameters. Merchant identification key not provided")
    private Long merchantId;
}
