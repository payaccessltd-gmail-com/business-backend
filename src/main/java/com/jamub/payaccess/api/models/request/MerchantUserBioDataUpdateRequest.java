package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class MerchantUserBioDataUpdateRequest {
    private Long userId;
    private String firstName;
    private String lastName;
    private String gender;
    private Date dateOfBirth;
    private String identificationDocument;
    private String identificationNumber;
    private String identificationDocumentPath;
}
