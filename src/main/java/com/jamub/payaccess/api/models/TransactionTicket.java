package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.ProductCategory;
import com.jamub.payaccess.api.enums.TicketStatus;
import com.jamub.payaccess.api.enums.Urgency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transaction_tickets")
public class TransactionTicket implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false)
    private Long transactionId;
    @Column(nullable= false)
    private Long merchantId;

    @Column(nullable= false)
    private Long createdByUserId;

    @Column(nullable= true)
    private String attachmentImage;

    @Column(nullable= false)
    private String ticketNumber;

    @Column(nullable= false)
    private String ticketMessage;

    @Column(nullable = false)
    LocalDateTime createdAt;
    @Column(nullable = false)
    LocalDateTime updatedAt;
    LocalDateTime deletedAt;


    @Column(nullable= true)
    @Enumerated(EnumType.STRING)
    private ProductCategory ticketCategory;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;





    @Column(nullable = true)
    Long assignedToUserId;


    @Column(nullable = true)
    Long closedByUserId;


}
