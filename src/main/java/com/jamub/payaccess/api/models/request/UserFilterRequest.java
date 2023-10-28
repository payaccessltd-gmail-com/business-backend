package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilterRequest {

    private UserRole role;
}
