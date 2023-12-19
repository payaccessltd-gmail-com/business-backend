package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.AcquirerStatus;
import com.jamub.payaccess.api.enums.TerminalBrand;
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
@Table(name="acquirers")
public class Acquirer implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	@Column(nullable = false)
	String acquirerName;
	@Column(nullable = false)
	String acquirerCode;
	@Column(nullable = false)
	Boolean isBank;
	@Column(nullable = false)
	Long bankId;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	AcquirerStatus acquirerStatus;



	@Column(nullable= false)
	private LocalDateTime createdAt;
	@Column(nullable= true)
	private LocalDateTime deletedAt;
	@Column(nullable= true)
	private LocalDateTime updatedAt;

}
