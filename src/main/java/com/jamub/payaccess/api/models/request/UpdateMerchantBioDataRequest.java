package com.jamub.payaccess.api.models.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jamub.payaccess.api.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateMerchantBioDataRequest {

    @NotBlank(message = "Incomplete request parameters. Email address is not provided")
    private String emailAddress;

    @NotBlank(message = "Incomplete request parameters. Gender is not provided")
    @Pattern(regexp = "FEMALE|MALE|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: FEMALE, MALE")
    private String gender;

    @NotBlank(message = "Incomplete request parameters. Date of birth is not provided")
    @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", flags = Pattern.Flag.UNICODE_CASE, message = "Invalid format of the Date of birth. Must be represented in the format yyyy-MM-dd")
    private String dateOfBirth;

    @NotBlank(message = "Incomplete request parameters. Specify the type of identification document is not provided")
    private String identificationDocument;

    @NotBlank(message = "Incomplete request parameters. Identification number is not provided")
    private String identificationNumber;

    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;

    @Valid
    @NotNull(message = "Incomplete request parameters. Identification document must be provided")
    MultipartFile identificationDocumentPath;
}
