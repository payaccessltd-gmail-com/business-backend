package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "merchant_approvals")
public class MerchantApproval implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false)
    private Long merchantId;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private MerchantStage merchantStage;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private MerchantReviewStatus merchantReviewStatus;

    @Column(nullable= false)
    private Long actedByUserId;

    @Column(nullable= false)
    private String merchantCode;

    @Column(nullable= false)
    private Boolean isValid;

    @Column(nullable = false)
    Date created_at;
    Date updated_at;
    Date deleted_at;

    @Column(nullable= false)
    private String details;




}
