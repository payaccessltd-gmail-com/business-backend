package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SwitchApiModeRequest {

    @NotNull(message = "Please specify if you are switching to live mode")
    private Boolean isLive;
}