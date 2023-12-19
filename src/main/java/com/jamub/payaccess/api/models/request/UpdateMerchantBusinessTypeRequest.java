package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class UpdateMerchantBusinessTypeRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Business type must be provided")
    @Pattern(regexp = "INDIVIDUAL|REGISTERED_BUSINESS|NGO_BUSINESS|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: INDIVIDUAL, REGISTERED_BUSINESS, NGO_BUSINESS")
    private String businessType;
    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;
}
