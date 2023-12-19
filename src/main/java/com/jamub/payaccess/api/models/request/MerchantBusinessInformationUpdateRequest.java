package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Getter @Setter
public class MerchantBusinessInformationUpdateRequest {
    @NotBlank(message = "Incomplete request parameters. Description of your business not provided")
    String businessDescription;

    @NotBlank(message = "Incomplete request parameters. Business email not provided")
    String businessEmail;

    @NotBlank(message = "Incomplete request parameters. Primary mobile number not provided")
    String primaryMobile;

    @NotBlank(message = "Incomplete request parameters. Country of operation not provided")
    String country;

    @NotBlank(message = "Incomplete request parameters. State of operation not provided")
    String businessState;

    @NotBlank(message = "Incomplete request parameters. Business website URL not provided")
    String businessWebsite;

    @NotBlank(message = "Incomplete request parameters. Merchant identification not provided")
    Long merchantId;

    @Valid
    MultipartFile businessLogoFile;
}
