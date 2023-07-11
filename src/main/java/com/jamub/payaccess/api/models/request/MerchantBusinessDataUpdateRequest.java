package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class MerchantBusinessDataUpdateRequest {
    private Long merchantId;
    private String businessDescription;
    private String businessEmail;
    private String primaryMobile;
    private String supportContact;
    private String businessCity;
    private String businessState;
    private String businessWebsite;
    private String businessLogo;
    private String businessCertificate;
}
