package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.TerminalBrand;
import com.jamub.payaccess.api.enums.TerminalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class CreateTerminalRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Terminal code is not provided")
    private String terminalCode;

    @NotBlank(message = "Incomplete request parameters. Terminal serial number not provided")
    private String terminalSerialNo;

    @NotBlank(message = "Incomplete request parameters. Terminal brand not provided")
    @Pattern(regexp = "TELPO|INDECO|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: TELPO, INDECO")
    @Schema(name = "Brand of the Terminal", example = "PENDING", allowableValues = {"TELPO", "INDECO"})
    private String terminalBrand;

    @NotNull(message = "Incomplete request parameters. Merchant identifier not provided")
    private Long merchantId;

    @NotBlank(message = "Incomplete request parameters. Terminal type not specified")
    @Pattern(regexp = "POS|ATM|MOBILE|WEB|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: POS, ATM, MOBILE, WEB")
    @Schema(name = "Type of the Terminal", example = "POS", allowableValues = {"POS", "ATM", "MOBILE", "WEB"})
    private String terminalType;

    @NotNull(message = "Incomplete request parameters. Acquirer not provided")
    private Long acquirerId;

    @NotNull(message = "Incomplete request parameters. Invalid terminal request identifier not provided")
    private Long terminalRequestId;

}
