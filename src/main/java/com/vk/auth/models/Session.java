package com.vk.auth.models;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	
	private Date expiringAt;
	
	private String deviceId;
	
	private String ipAddress;
	
	@Enumerated(EnumType.ORDINAL)
	private SessionStatus sessionStatus;

}
