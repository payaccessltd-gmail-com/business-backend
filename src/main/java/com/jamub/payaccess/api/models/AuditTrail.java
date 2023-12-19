package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.ProductCategory;
import com.jamub.payaccess.api.enums.Urgency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "audit_trails")
public class AuditTrail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false)
    private Long actorUserId;
    private String actorFullName;


    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private ApplicationAction userAction;


    @Column(nullable = false)
    private Date createdAt;


    private String description;
    private String ipAddress;

    private Long objectIdReference;
    private String objectClassReference;




}
