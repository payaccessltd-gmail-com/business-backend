package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.Urgency;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class CreateFeedbackRequest extends BaseRequest{

    private String emailAddress;
    private String title;
    private String productCategory;
    private String description;
    private Urgency urgency;
    private MultipartFile businessLogoFile;
}
