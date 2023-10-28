package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class MerchantBusinessInformationUpdateRequest {
    String businessDescription;
    String businessEmail;
    String primaryMobile;
    String country;
    String businessState;
    String businessWebsite;
    Long merchantId;
    MultipartFile businessLogoFile;
}
