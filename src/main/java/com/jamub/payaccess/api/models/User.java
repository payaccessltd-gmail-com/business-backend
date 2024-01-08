package com.jamub.payaccess.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jamub.payaccess.api.enums.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "users")
@JsonIgnoreProperties(value = { "password", "otp", "otpExpiryDate", "verificationLink", "forgotPasswordLink" })
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    @Column(nullable= false, unique=true)
    private String emailAddress;
    @Column(nullable= true)
    private String mobileNumber;
    @Column(nullable= false)
    private String password;
    @Column(nullable= true)
    private String forgotPasswordLink;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Date dateOfBirth;
    @Column(nullable= true)
    private String otp;
    @Column(nullable= true)
    private LocalDateTime otpExpiryDate;
    @Column(nullable= true)
    private String verificationLink;
    @Column(nullable= true)
    private String primaryBusinessName;

    private String country;
//
    @Column(nullable= true)
    private Boolean softwareDeveloper;
    @Enumerated(EnumType.STRING)
    private IdentificationDocument identificationDocument;
    private String identificationNumber;
    private String identificationDocumentPath;
    private Long primaryMerchantId;
    private Boolean twoFactorAuthForLogin;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    @Column(nullable= false)
    private Date createdAt;
    @Column(nullable= true)
    private Date deletedAt;
    @Column(nullable= true)
    private Date updatedAt;
}
