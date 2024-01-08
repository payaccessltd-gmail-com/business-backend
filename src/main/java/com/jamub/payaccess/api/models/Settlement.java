package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name="settlements")
public class Settlement implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	@Column(nullable = false)
	Long createdByUserId;
	@Column(nullable = false)
	BigDecimal settlementAmount;
	@Column(nullable = false)
	String settlementCode;
	@Column(nullable = false)
	LocalDateTime createdAt;
	LocalDateTime deletedAt;
	@Column(nullable = false)
	LocalDateTime updatedAt;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	SettlementStatus settlementStatus;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	PayAccessCurrency payAccessCurrency;
	@Column(nullable = false)
	LocalDate settlementDate;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

}
