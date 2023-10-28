package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.BusinessType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddMerchantRequest extends BaseRequest{
    private BusinessType businessType;
    private String businessName;
}
