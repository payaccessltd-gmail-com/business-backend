package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class CustomerPinUpdateRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Pin not provided")
    private String pin;
}
