package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CloseTransactionTicketRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Specify the ticket identifier")
    private String ticketNumber;
}
