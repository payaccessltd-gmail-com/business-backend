package com.jamub.payaccess.api.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name="country_states")
public class CountryState implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	@Column(nullable = false)
	String name;
	@Column(nullable = false)
	String code;
	@Column(nullable = false)
	String country_id;


	@Column(nullable= false)
	private LocalDateTime createdAt;
	@Column(nullable= true)
	private LocalDateTime deletedAt;
	@Column(nullable= true)
	private LocalDateTime updatedAt;
}
