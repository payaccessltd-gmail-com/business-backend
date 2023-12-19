package com.jamub.payaccess.api.models.request;


import com.jamub.payaccess.api.enums.TerminalBrand;
import com.jamub.payaccess.api.enums.TerminalStatus;
import com.jamub.payaccess.api.enums.TerminalType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@Setter
public class TerminalFilterRequest {
    @Column(nullable = false)
    @NotNull(message = "Incomplete request parameters. Terminal status not provided")
    @Pattern(regexp = "ACTIVE|DEACTIVATED|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: ACTIVE, DEACTIVATED")
    private TerminalStatus terminalStatus;

    @NotNull(message = "Incomplete request parameters. Acquirer identification not provided")
    private Long acquirerId;

    @NotNull(message = "Incomplete request parameters. Terminal brand not provided")
    @Pattern(regexp = "TELPO|INDECO|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: TELPO, INDECO")
    private TerminalBrand terminalBrand;

    @NotNull(message = "Incomplete request parameters. Terminal type not provided")
    @Pattern(regexp = "POS|ATM|MOBILE|WEB|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: POS, ATM, MOBILE, WEB")
    private TerminalType terminalType;

    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;
}
