package com.jamub.payaccess.api.models;

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
public class Merchant  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false, unique=true)
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
    @Column(nullable= false)
    private String businessLogo;
    @Column(nullable= false)
    private String businessCertificate;
    @Column(nullable= false)
    private String businessBvn;
    @Column(nullable= false)
    private String businessBankName;
    @Column(nullable= false)
    private String businessAccountNumber;
    @Column(nullable= false)
    private String businessAccountName;


}
