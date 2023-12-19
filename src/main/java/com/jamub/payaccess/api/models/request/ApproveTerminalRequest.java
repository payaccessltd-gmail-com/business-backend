package com.jamub.payaccess.api.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ApproveTerminalRequest extends BaseRequest{

    @NotEmpty(message = "Serial Number list cannot be empty.")

    @Schema(name = "Category of Business", description = "Array of serial numbers of the terminals")
    private List<String> serialNumberList;
}
