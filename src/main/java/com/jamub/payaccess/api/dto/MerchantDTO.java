package com.jamub.payaccess.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jamub.payaccess.api.enums.BusinessCategory;
import com.jamub.payaccess.api.enums.BusinessType;
import com.jamub.payaccess.api.enums.Country;
import com.jamub.payaccess.api.enums.MerchantStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantDTO {

    private String businessName;
    @Enumerated(EnumType.STRING)
    private BusinessCategory businessCategory;
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;
    @Enumerated(EnumType.STRING)
    private MerchantStatus merchantStatus;


    private String businessDescription;
    private String businessEmail;
    private String primaryMobile;
    private String supportContact;
    private String businessCity;
    private String businessState;
    private String businessWebsite;
    private String businessLogo;
    private String businessCertificate;
    private String businessBvn;
    private String businessBankName;
    private String businessAccountNumber;
    private String businessAccountName;
}
