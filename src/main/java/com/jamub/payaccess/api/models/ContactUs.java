package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.ProductCategory;
import com.jamub.payaccess.api.enums.Urgency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "contact_us")
public class ContactUs implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String subject;
    private String emailAddress;

    @Column(nullable= true)
    private Long userId;


    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private Urgency urgency;
    
    @Column(nullable= false)
    private String description;

    @Column(nullable= false)
    private String attachment;




}
