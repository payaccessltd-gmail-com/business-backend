package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter @Setter
public class MerchantBusinessDataUpdateRequest {

    private String businessDescription;
    private String businessEmail;
    private String primaryMobile;
    private String supportContact;
    private String businessCity;
    private String businessState;
    private String businessWebsite;
    private String businessLogo;
    private String businessCertificateFile;
    private Long merchantId;
    private String country;
    private String businessAddress;
}
