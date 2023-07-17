package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.BusinessCategory;
import com.jamub.payaccess.api.enums.BusinessType;
import com.jamub.payaccess.api.enums.Country;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MerchantSignUpRequest extends BaseRequest{
    private String country;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String password;
    private String businessName;
    private String businessCategory;
    private String businessType;
    private String isSoftwareDeveloper;
}
