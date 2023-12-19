package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserFilterRequest {
    @NotBlank(message = "Incomplete request parameters. Users role not provided")
    private UserRole role;
}
