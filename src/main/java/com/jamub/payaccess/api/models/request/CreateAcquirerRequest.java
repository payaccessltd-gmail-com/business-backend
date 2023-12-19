package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.Urgency;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class CreateAcquirerRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Acquirer name not provided")
    @Schema(name = "Acquirer name", example = "ABC Ltd")
    private String acquirerName;
    @NotBlank(message = "Incomplete request parameters. Acquirer code not provided")
    @Schema(name = "Acquirer code", example = "ABC")
    private String acquirerCode;
    @NotBlank(message = "Incomplete request parameters. Bank code not provided")
    @Schema(name = "CBN Bank Code", example = "033")
    private String bankCode;
    @NotNull(message = "Incomplete request parameters. Specify if this acquirer is a bank")
    @Schema(name = "Is this a bank", example = "false")
    private Boolean isBank;
}
