package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.MakerCheckerType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class MakerCheckerUser implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	@Column(nullable = false)
	String emailAddress;
	@Column(nullable = false)
	String firstName;
	@Column(nullable = false)
	String lastName;
	@Column(nullable = false)
	String userRole;
	@Column(nullable = false)
	Long userId;
	@Column(nullable = false)
	Integer checkerLevel;

	@Column(nullable= false)
	@Enumerated(EnumType.STRING)
	private MakerCheckerType makerCheckerType;

	@Column(nullable= false)
	private LocalDateTime createdAt;
	@Column(nullable= true)
	private LocalDateTime deletedAt;
	@Column(nullable= true)
	private LocalDateTime updatedAt;
}
