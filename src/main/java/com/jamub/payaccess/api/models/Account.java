package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.AccountStatus;
import com.jamub.payaccess.api.enums.AccountType;
import com.jamub.payaccess.api.enums.PayAccessCurrency;
import com.jamub.payaccess.api.services.AccountService;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name="accounts")  
public class Account implements Serializable {  
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long id;
	Long customerId;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	AccountStatus status;
	@Column(nullable = false)
	Date created_at;
	Date updated_at;
	Date deleted_at;
	@Column(nullable = false)
	String currencyCode;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	AccountType accountType;
	@GeneratedValue(strategy=GenerationType.AUTO)
	String walletNumber;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	PayAccessCurrency payAccessCurrency;
	@Column(precision=10, scale=2, nullable = false)
	Double accountBalance;
	@Column(precision=10, scale=2, nullable = false)
	Double floatingBalance;
	Long restrictedToUseOnDeviceId;
	@Column(nullable = false)
	Integer isLive;
	@Column(nullable = false)
	String accountName;
	String hashedPin;
	@Column(nullable = false)
	Long accountPackageId;


	public Account() {
//		this.created_at = new Date();
//		this.updated_at = new Date();
	}

	@PrePersist
	protected void onCreate() {
		this.created_at = new Date();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updated_at = new Date();
	}

	public synchronized Account withdraw(AccountService accountService, Double amt) throws Exception{
		synchronized (this.id) {
			this.accountBalance= (this.accountBalance==null ? 0 : this.accountBalance)  -amt;
			this.floatingBalance= (this.floatingBalance==null ? 0 : this.floatingBalance) -amt;
//			accountService.updateRecord(this);
			return this;
		}
	}
	
	
	public synchronized Account deposit(AccountService accountService,
			Double amount) throws Exception
	{
		synchronized (this.id) {
			this.accountBalance= 
					(this.accountBalance==null ? 0 : this.accountBalance)+
					amount;
			this.floatingBalance = (this.floatingBalance==null ? 0 : this.floatingBalance) + amount;



//			accountService.updateRecord(this);
			
			return this;
		}
	}
}
