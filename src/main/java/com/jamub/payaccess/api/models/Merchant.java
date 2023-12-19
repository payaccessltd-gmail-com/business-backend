package com.jamub.payaccess.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jamub.payaccess.api.enums.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "merchants")
@JsonIgnoreProperties(value = { "secretKeyLive", "publicKeyLive", "secretKey", "publicKey" })
public class Merchant  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false)
    private String businessName;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private BusinessCategory businessCategory;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private MerchantStatus merchantStatus;

    @Column(nullable= false)
    private Long userId;


    @Column(nullable= false)
    private String businessDescription;
    @Column(nullable= false)
    private String businessEmail;
    @Column(nullable= false)
    private String primaryMobile;
    @Column(nullable= false)
    private String supportContact;
    @Column(nullable= false)
    private String businessCity;
    @Column(nullable= false)
    private String businessState;
    @Column(nullable= false)
    private String businessWebsite;
    @Column(nullable= true)
    private String businessLogo;
    @Column(nullable= true)
    private String businessCertificateFile;
    @Column(nullable= false)
    private String businessBvn;
    @Column(nullable= false)
    private String businessBankName;
    @Column(nullable= false)
    private String businessAccountNumber;
    @Column(nullable= false)
    private String businessAccountName;
    @Column(nullable= false)
    private String merchantCode;
    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private APIMode apiMode;
    @Column(nullable= true, unique=true)
    private String secretKey;
    @Column(nullable= true, unique=true)
    private String publicKey;
    @Column(nullable= true, unique=true)
    private String secretKeyLive;
    @Column(nullable= true, unique=true)
    private String publicKeyLive;

    @Column(nullable= false)
    private String webhookUrl;
    @Column(nullable= false)
    private String callbackUrl;
    @Column(nullable= true)
    private String payAccessUsage;

    @Column(nullable= true)
    String governmentApprovedDocumentFileName;
    @Column(nullable= true)
    String directorsProofOfIdentityFileName;
    @Column(nullable= true)
    String businessOwnersDocumentFileName;
    @Column(nullable= true)
    String shareholdersDocumentFileName;

    @Column(nullable = false)
    LocalDateTime createdAt;
    LocalDateTime deletedAt;
    LocalDateTime updatedAt;

    @Column(nullable = false)
    Boolean kycSet;
    @Column(nullable = false)
    Boolean businessInfoSet;
    @Column(nullable = false)
    Boolean personalInfoSet;
    @Column(nullable = false)
    Boolean accountInfoSet;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
