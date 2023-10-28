package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.TerminalBrand;
import com.jamub.payaccess.api.enums.TerminalRequestStatus;
import com.jamub.payaccess.api.enums.TerminalStatus;
import com.jamub.payaccess.api.enums.TerminalType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name="terminals")
public class Terminal implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long id;
	@Column(nullable = false)
	Long merchantId;
	@Column(nullable = false)
	Long ownedByUserId;
	@Column(nullable = false)
	String terminalCode;
	@Column(nullable = false)
	String terminalKey;
	String serialNo;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	TerminalType terminalType;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	TerminalBrand terminalBrand;
	@Column(nullable = false)
	Long terminalRequestId;
	@Column(nullable = false)
	LocalDateTime createdAt;
	LocalDateTime deletedAt;
	LocalDateTime updatedAt;
	@Column(nullable = false)
	Long acquirerId;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	TerminalStatus terminalStatus;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

}
