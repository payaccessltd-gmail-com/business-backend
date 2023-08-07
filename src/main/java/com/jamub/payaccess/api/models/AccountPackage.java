package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.PayAccessCurrency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


@Getter
@Setter
@Entity
@Table(name = "account_packages")
public class AccountPackage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false)
    private String packageName;


    @Enumerated(EnumType.STRING)
    @Column(nullable= false)
    private PayAccessCurrency payAccessCurrency;

    private String otherDetails;
}
