package com.jamub.payaccess.api.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jamub.payaccess.api.enums.EmailDocumentPriorityLevel;
import com.jamub.payaccess.api.enums.EmailDocumentStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "email_documents")
public class EmailDocument implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false, columnDefinition = "TEXT")
    private String htmlMessage;

    @Column(nullable= false)
    private String recipients;

    @Column(nullable= true)
    private String attachmentList;

    @Column(nullable= false)
    private String subject;

    @Column(nullable= false)
    private String createdByUserId;

    @Enumerated(EnumType.STRING)
    EmailDocumentStatus emailDocumentStatus;

    @Enumerated(EnumType.STRING)
    EmailDocumentPriorityLevel emailDocumentPriorityLevel;
}
