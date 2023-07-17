package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.BusinessCategory;
import com.jamub.payaccess.api.enums.BusinessType;
import com.jamub.payaccess.api.enums.CustomerStatus;
import com.jamub.payaccess.api.enums.MerchantStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false, unique=true)
    private String mobileNumber;

    private String otp;
    private String state;
    private String city;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus;

    @Column(nullable= false)
    private Long userId;
    private LocalDate dateOfBirth;
    private String address;

}
