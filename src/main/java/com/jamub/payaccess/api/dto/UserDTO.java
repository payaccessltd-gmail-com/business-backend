package com.jamub.payaccess.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jamub.payaccess.api.enums.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

    @Enumerated(EnumType.STRING)
    private Country country;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private boolean isSoftwareDeveloper;
    private String verificationLink;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
}
