package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.BusinessCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class AddNewMerchantRequest extends BaseRequest{
    @NotBlank(message = "Please specify the business category")
    @Schema(name = "Category of Business", example = "TRANSPORTATION", allowableValues = {"TRANSPORTATION", "FARMING", "FISHING"})
    private String businessCategory;

    @NotBlank(message = "Please specify the business type")
    @Schema(name = "Type of Business", example = "INDIVIDUAL", allowableValues = {"INDIVIDUAL", "REGISTERED_BUSINESS", "NGO_BUSINESS"})
    private String businessType;

    @NotNull(message = "Please specify if you are a software developer")
    private Boolean softwareDeveloper;

    @NotBlank(message = "Please specify the country your merchant operates in")
    @Schema(name = "Type of Business", example = "INDIVIDUAL", description = "Must be one of the countries listed in Settings Controller >> Get Country List")
    private String country;

    @NotBlank(message = "Please provide your mobile number")
    private String mobileNumber;

    @NotBlank(message = "Invalid request. Merchant identification not provided")
    private Long merchantId;
}
