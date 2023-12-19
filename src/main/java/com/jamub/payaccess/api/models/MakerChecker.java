package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.BusinessCategory;
import com.jamub.payaccess.api.enums.MakerCheckerType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name="maker_checker")
public class MakerChecker implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
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
