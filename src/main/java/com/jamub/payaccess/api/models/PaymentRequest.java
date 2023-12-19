package com.jamub.payaccess.api.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jamub.payaccess.api.enums.MakerCheckerType;
import com.jamub.payaccess.api.enums.PaymentRequestType;
import com.jamub.payaccess.api.serializer.JsonDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name="payment_requests")
public class PaymentRequest implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	@Column(nullable = false)
	String merchantCode;
	@Column(nullable = true)
	String terminalCode;
	@Column(nullable = false)
	String orderRef;

	@Column(nullable= false)
	@Enumerated(EnumType.STRING)
	private PaymentRequestType paymentRequestType;
	@Column(nullable = true)
	String requestBody;
	@Column(nullable = true)
	String responseBody;

	@Column(nullable= false)
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	private LocalDateTime createdAt;
	@Column(nullable= true)
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	private LocalDateTime deletedAt;
	@Column(nullable= false)
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	private LocalDateTime updatedAt;
}
