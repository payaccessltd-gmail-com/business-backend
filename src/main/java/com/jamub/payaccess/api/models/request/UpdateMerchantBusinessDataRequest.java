package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
public class UpdateMerchantBusinessDataRequest {

    @NotBlank(message = "Incomplete request parameters. Description of the business is not provided")
    private String businessDescription;

    @NotBlank(message = "Incomplete request parameters. Business email address is not provided")
    private String businessEmail;

    @NotBlank(message = "Incomplete request parameters. Primary mobile number of the business is not provided")
    private String primaryMobile;

    @NotBlank(message = "Incomplete request parameters. Contact number or email for your business support  is not provided")
    private String supportContact;

    @NotBlank(message = "Incomplete request parameters. City of operation of the business is not provided")
    private String businessCity;

    @NotBlank(message = "Incomplete request parameters. State of operation of the business is not provided")
    private String businessState;

    @NotBlank(message = "Incomplete request parameters. Country of operation of the business is not provided")
    private String businessCountry;

    @NotBlank(message = "Incomplete request parameters. Business website is not provided")
    private String businessWebsite;

    @NotNull(message = "Incomplete request parameters. Invalid merchant identification is not provided")
    private Long merchantId;

    @NotBlank(message = "Incomplete request parameters. Address of the business is not provided")
    private String businessAddress;

//    @Valid
//    @NotNull(message = "Incomplete request parameters. Logo of your business must be provided")
    private MultipartFile businessLogoFile;

//    @Valid
//    @NotNull(message = "Incomplete request parameters. Certificate of Incorporation of your business must be provided")
    private MultipartFile businessCertificateFile;

}
