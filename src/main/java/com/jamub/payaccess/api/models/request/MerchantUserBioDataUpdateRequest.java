package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Date;

@Getter @Setter
public class MerchantUserBioDataUpdateRequest {

    @NotBlank(message = "Incomplete request parameters. Email address not provided")
    private String emailAddress;

    @NotBlank(message = "Incomplete request parameters. Gender not provided")
    private String gender;

    @NotBlank(message = "Incomplete request parameters. Date of birth not provided")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Incomplete request parameters. Identification document not provided")
    private String identificationDocument;

    @NotBlank(message = "Incomplete request parameters. Identification number not provided")
    private String identificationNumber;

    @NotBlank(message = "Incomplete request parameters. Identification document path not provided")
    private String identificationDocumentPath;

    @NotBlank(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;
}
