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
    private String country;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private Boolean softwareDeveloper;
    private String verificationLink;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
}
