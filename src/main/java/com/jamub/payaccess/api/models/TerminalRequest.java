package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.*;
import com.jamub.payaccess.api.services.AccountService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Getter
@Setter
@Table(name="terminal_requests")
public class TerminalRequest implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	@Column(nullable = false)
	Integer quantity;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	TerminalRequestStatus terminalRequestStatus;
	@Column(nullable = false)
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
	LocalDateTime deletedAt;
	@Column(nullable = false)
	Long merchantId;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	TerminalBrand terminalBrand;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	TerminalType terminalType;
	@Column(nullable = true)
	Long approvedByUserId;
	@Column(nullable = false)
	Long createdByUserId;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

}
