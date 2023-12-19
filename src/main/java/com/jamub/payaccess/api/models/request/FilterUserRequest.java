package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class FilterUserRequest extends BaseRequest{

    @Schema(name = "Start date user as created", description = "Format yyyy-MM-dd")
    private String startDate;

    @Schema(name = "End date user as created", description = "Format yyyy-MM-dd")
    private String endDate;

    @Schema(name = "Role of the User", description = "See API in Settings Controller >> Get Role List")
    private String userRole;

    @Schema(name = "Status of the User", example = "NOT_ACTIVATED", allowableValues = {"NOT_ACTIVATED", "ACTIVE", "SUSPENDED", "DELETED"})
    private UserStatus userStatus;
}
