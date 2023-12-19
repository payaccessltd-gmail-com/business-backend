package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class CreateEmailDocumentRequest extends BaseRequest{

    private String htmlMessage;
    private String recipients;
    private String attachmentList;
    private String subject;
    private Long createdByUserId;
}
