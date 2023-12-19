package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.Urgency;
import com.jamub.payaccess.api.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class UpdateUserStatusRequest extends BaseRequest{

    @NotNull(message = "Incomplete request parameters. User identification not provided")
    private Long userId;

    @NotNull(message = "Incomplete request parameters. User status not provided")
    @Pattern(regexp = "ACTIVE|SUSPENDED|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: ACTIVE, SUSPENDED")
    private String userStatus;
}
