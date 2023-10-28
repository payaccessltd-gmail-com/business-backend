package com.jamub.payaccess.api.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Card {
    @MaskPan
    ('*')
    private String pan;

    private String expDate;
    private String cvv;
    private String pin;
}
