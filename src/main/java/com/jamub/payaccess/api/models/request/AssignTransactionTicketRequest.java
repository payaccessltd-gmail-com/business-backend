package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AssignTransactionTicketRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Specify the ticket identifier")
    private String ticketNumber;

    @NotNull(message = "Incomplete request parameters. Specify who you are assigning this ticket to")
    private Long assignToUserId;
}
