package com.jamub.payaccess.api.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class Card {
    @NotBlank(message = "Incomplete request parameters. Card number not provided")
    @MaskPan
    ('*')
    private String pan;
    @NotBlank(message = "Incomplete request parameters. Card expiration date not provided")
    private String expDate;
    @NotBlank(message = "Incomplete request parameters. Card CVV not provided")
    private String cvv;
    @NotBlank(message = "Incomplete request parameters. Card pin not provided")
    private String pin;
}
