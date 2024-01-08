package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.PayAccessCurrency;
import com.jamub.payaccess.api.enums.SettlementStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name="settlement_breakdown")
public class SettlementBreakdown implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	@Column(nullable = false)
	Long settlementId;
	@Column(nullable = false)
	BigDecimal settlementAmount;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	PayAccessCurrency payAccessCurrency;
	@Column(nullable = false)
	Long merchantId;
	@Column(nullable = false)
	String merchantCode;
	@Column(nullable = false)
	String businessName;
	@Column(nullable = false)
	LocalDateTime createdAt;
	LocalDateTime deletedAt;
	@Column(nullable = false)
	LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

}
