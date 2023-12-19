package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.Urgency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class AuditTrailFilterRequest extends BaseRequest{

    @NotBlank(message = "Please specify the start date for the search of the Audit Trail")
    private String startDate;
    @NotBlank(message = "Please specify the end date for the search of the Audit Trail")
    private String endDate;
    private Long actorUserId;
    @Schema(name = "Action carried out by users", example = "TRANSPORTATION", allowableValues = {"UPDATE_USER_STATUS", "CREATE_NEW_ADMIN_USER", "UPDATE_ADMIN_USER", "CREATE_NEW_BANK", "CREATE_TRANSACTION_TICKET", "CREATE_MAKER_CHECKER", "MAP_PERMISSION_TO_ROLE", "CREATE_NEW_ACQUIRER", "CLOSE_TRANSACTION_TICKET", "ASSIGN_TRANSACTION_TICKET"})
    private String auditTrailAction;
}
