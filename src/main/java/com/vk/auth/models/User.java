package com.vk.auth.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User extends BaseModel {
	
	private String email;
	
	private String passwordSalt;
	
	private String gender;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<UserRole> roles = new HashSet<>();

}
