package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class ISWAuthTokenRequest implements Serializable {
    private String grant_type = "client_credentials";
}
