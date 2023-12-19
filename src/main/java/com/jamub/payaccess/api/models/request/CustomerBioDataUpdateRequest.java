package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Date;

@Getter @Setter
public class CustomerBioDataUpdateRequest {

    @NotBlank(message = "Incomplete request parameters. First name not provided")
    private String firstName;

    @NotBlank(message = "Incomplete request parameters. Last name not provided")
    private String lastName;

    @NotBlank(message = "Incomplete request parameters. Gender not provided")
    @Schema(name = "Category of the issue", example = "FEMALE", allowableValues = {"FEMALE", "MALE"})
    private String gender;

    @NotBlank(message = "Incomplete request parameters. Country not provided")
    @Schema(name = "Category of the issue", description = "Use the api in the SettingsContoller >> Get Country List.")
    private String country;

    @NotBlank(message = "Incomplete request parameters. State of operation not provided")
    @Schema(name = "Category of the issue", description = "For NIGERIA, use the api in the SettingsContoller >> Get States List.")
    private String state;

    @NotBlank(message = "Incomplete request parameters. City of operation not provided")
    private String city;

    @NotBlank(message = "Incomplete request parameters. Customers date of birth was not provided")
    @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", flags = Pattern.Flag.UNICODE_CASE, message = "Invalid format of the Date of birth. Must be represented in the format yyyy-MM-dd")
    @Schema(name = "Category of the issue", description = "Format yyyy-MM-dd")
    private String dateOfBirth;

    @NotBlank(message = "Incomplete request parameters. Customers address was not provided")
    private String address;
}
