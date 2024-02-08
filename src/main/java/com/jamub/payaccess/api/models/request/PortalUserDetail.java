package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.PendingRequestStatus;
import com.jamub.payaccess.api.enums.PendingRequestType;
import com.jamub.payaccess.api.models.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PortalUserDetail {

    @NotBlank(message = "Incomplete request parameters. Request Type not provided")
    private PendingRequestType requestType;
    @NotBlank(message = "Incomplete request parameters. Request Status number not provided")
    private PendingRequestStatus status;
    @NotBlank(message = "Incomplete request parameters. Portal user not provided")
    private User user;
    @NotBlank(message = "Incomplete request parameters. Request Description not provided")
    private String description;
    @NotBlank(message = "Incomplete request parameters. Request Date not provided")
    private Date requestDate;
    private String systemIp;

}
