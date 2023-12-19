package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
public class UpdateMerchantCountryRequest {

    @NotBlank(message = "Incomplete request parameters. Country not provided")
    private String country;
}
