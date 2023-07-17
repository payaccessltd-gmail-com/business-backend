package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter @Setter
public class CustomerBioDataUpdateRequest {
    private Long userId;
    private String firstName;
    private String lastName;
    private String gender;
    private String country;
    private String state;
    private String city;
    private LocalDate dateOfBirth;
    private String address;
}
