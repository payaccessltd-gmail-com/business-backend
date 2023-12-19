package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.AcquirerStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name="banks")
public class Bank implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	@Column(nullable = false)
	String bankName;
	@Column(nullable = false)
	String bankOtherName;
	@Column(nullable = false)
	String bankCode;


	@Column(nullable= false)
	private LocalDateTime createdAt;
	@Column(nullable= true)
	private LocalDateTime deletedAt;
	@Column(nullable= true)
	private LocalDateTime updatedAt;
}
