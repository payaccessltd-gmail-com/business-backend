package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomerPinUpdateRequest extends BaseRequest{
    private String pin;
}
