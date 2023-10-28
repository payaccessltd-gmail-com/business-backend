package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateBioDataRequest extends BaseRequest{
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String gender;
    private String phoneNumer;
}
