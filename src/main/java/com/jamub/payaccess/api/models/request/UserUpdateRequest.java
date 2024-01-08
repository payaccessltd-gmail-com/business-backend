package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class UserUpdateRequest extends BaseRequest{


    @NotBlank(message = "Incomplete request parameters. First name not provided")
    private String firstName;

    @NotBlank(message = "Incomplete request parameters. Last name not provided")
    private String lastName;

    @NotBlank(message = "Incomplete request parameters. Email address not provided")
    private String emailAddress;

    @NotBlank(message = "Incomplete request parameters. Phone number not provided")
    private String phoneNumber;

    @NotNull(message = "Incomplete request parameters. Users role not provided")
    @Pattern(regexp = "MERCHANT|ADMINISTRATOR|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: MERCHANT, ADMINISTRATOR")
    private String userRole;

    @NotNull(message = "Incomplete request parameters. User identification not provided")
    private Long userId;
}
