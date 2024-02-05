package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.PendingRequestModule;
import com.jamub.payaccess.api.enums.PendingRequestStatus;
import com.jamub.payaccess.api.enums.PendingRequestType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "pending_request")
public class PendingRequest {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PendingRequestType requestType;
    @ManyToOne
    private User requestBy;
    @Enumerated(EnumType.STRING)
    private PendingRequestStatus status;
    private String description;
    private String branchCode;
    private String requestDate;
    @Column(length = 10000)
    private String additionalInfo;
    private Date actionOn;
    @ManyToOne
    private User actionBy;
    @ManyToOne
    private User authorizer;
    @Enumerated(EnumType.STRING)
    private PendingRequestModule module;
}