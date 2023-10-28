package com.jamub.payaccess.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jamub.payaccess.api.enums.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "feedback")
public class Feedback implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false)
    private String title;
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




}
