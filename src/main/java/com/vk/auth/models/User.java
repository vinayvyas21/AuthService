package com.vk.auth.models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User extends BaseModel {
	
	private String email;
	
	private String passwordSalt;
	
	private String gender;
	
	@OneToMany
	List<UserRole> roles;

}
