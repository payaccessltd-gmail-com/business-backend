package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.BusinessType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class AddMerchantRequest extends BaseRequest{
    @NotBlank(message = "Please specify the business type")
    @Pattern(regexp = "INDIVIDUAL|REGISTERED_BUSINESS|NGO_BUSINESS|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: INDIVIDUAL, REGISTERED_BUSINESS, NGO_BUSINESS")
    @Schema(name = "Type of Business", example = "INDIVIDUAL", allowableValues = {"INDIVIDUAL", "REGISTERED_BUSINESS", "NGO_BUSINESS"})
    private String businessType;
    @NotBlank(message = "Please provide the name of your merchant company")
    private String businessName;
}
