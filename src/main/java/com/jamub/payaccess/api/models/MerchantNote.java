package com.jamub.payaccess.api.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "merchant_notes")
public class MerchantNote implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false)
    private Long merchantId;
    @Column(nullable= false)
    private String details;
    @Column(nullable= false)
    private Long createdByUserId;
    @Column(nullable= false)
    private LocalDateTime createdAt;
    @Column(nullable= false)
    private LocalDateTime updatedAt;
    @Column(nullable= true)
    private LocalDateTime deletedAt;




}
