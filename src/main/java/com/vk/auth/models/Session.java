package com.vk.auth.models;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Session extends BaseModel {
	
	@ManyToOne
	private User user;
	
	private String token;
	
	private Date expiryDate;
	
	private String deviceId;
	
	private String ipAddress;

}
