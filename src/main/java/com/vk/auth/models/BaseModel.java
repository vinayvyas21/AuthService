package com.vk.auth.models;

import java.time.LocalDateTime;
import java.util.Date;

import com.vk.auth.listeners.AuditListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditListener.class)
public class BaseModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	private LocalDateTime deletedAt;

}
