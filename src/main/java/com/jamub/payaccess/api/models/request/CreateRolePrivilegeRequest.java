package com.jamub.payaccess.api.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
public class CreateRolePrivilegeRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Users role not provided")
    private String userRole;

    @NotEmpty(message = "Incomplete request parameters. Permission not provided")
    @Schema(name = "Category of Issues", description = "See RoleController >> Get Permission List Endpoint for all Permissions")
    private List<String> permission;
}
