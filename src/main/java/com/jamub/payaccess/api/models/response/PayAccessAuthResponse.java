package com.jamub.payaccess.api.models.response;


import com.jamub.payaccess.api.enums.UserRole;
import com.jamub.payaccess.api.models.AuthMerchantData;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.UserRolePermission;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PayAccessAuthResponse {
    private String subject;
    private Integer status;
    private String token;
    private UserRole role;
    private List<UserRolePermission> permissionList;
    private List<AuthMerchantData> merchantList;
    private String message;
}
