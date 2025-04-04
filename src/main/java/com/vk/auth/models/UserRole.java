package com.vk.auth.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserRole extends BaseModel {
	
	String name;

}
