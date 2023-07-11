package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private Country country;

    @Column(nullable= false)
    private String firstName;
    @Column(nullable= false)
    private String lastName;
    @Column(nullable= false)
    private String emailAddress;
    @Column(nullable= false)
    private String password;

    @Column(nullable= false)
    private boolean isSoftwareDeveloper;
    @Column(nullable= true)
    private String verificationLink;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Date dateOfBirth;
    @Enumerated(EnumType.STRING)
    private IdentificationDocument identificationDocument;
    private String identificationNumber;
    private String identificationDocumentPath;
}
