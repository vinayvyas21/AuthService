package com.vk.auth.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends BaseModel {
	
	private String email;
	
	private String passwordSalt;
	
	private String gender;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Role> roles = new HashSet<>();

}
