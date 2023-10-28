package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.Urgency;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class CreateContactUsRequest extends BaseRequest{

    private String emailAddress;
    private String subject;
    private String productCategory;
    private String description;
    private Urgency urgency;
    private MultipartFile businessLogoFile;
}
