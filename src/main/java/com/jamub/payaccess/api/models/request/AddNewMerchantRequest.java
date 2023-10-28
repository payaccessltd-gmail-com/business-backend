package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddNewMerchantRequest extends BaseRequest{
    private String businessCategory;
    private String businessType;
    private boolean softwareDeveloper;
    private String country;
    private String mobileNumber;
    private Long merchantId;
}
