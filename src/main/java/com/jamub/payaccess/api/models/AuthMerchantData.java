package com.jamub.payaccess.api.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthMerchantData{
    private Long id;
    private String businessName;
    private String merchantCode;
    private String businessLogo;
}