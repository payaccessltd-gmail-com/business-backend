package com.jamub.payaccess.api.dto;

import com.jamub.payaccess.api.enums.SettlementStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
public class SettlementTransactionDTO implements Serializable {
	@Column(nullable = false)
	BigDecimal settlementAmount;
	@Column(nullable = false)
	Long merchantId;
	@Column(nullable = false)
	String merchantCode;
	@Column(nullable = false)
	String businessName;
}
