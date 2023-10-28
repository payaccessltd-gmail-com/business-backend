package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;

@Getter @Setter
public class MerchantUserBioDataUpdateRequest {
    private String emailAddress;
    private String gender;
    private LocalDate dateOfBirth;
    private String identificationDocument;
    private String identificationNumber;
    private String identificationDocumentPath;
    private Long merchantId;
}
