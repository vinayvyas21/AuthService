package com.vk.auth.listeners;

import java.time.LocalDateTime;

import com.vk.auth.models.User;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;

public class AuditListener {
	
	@PreRemove
    public void setDeletedAt(Object entity) {
        if (entity instanceof User) {
        	User user = (User)entity;
        	user.setDeletedAt(LocalDateTime.now());
        }
    }
	
	@PostPersist
	public void setCreatedAt(Object entity) {
        if (entity instanceof User) {
        	User user = (User)entity;
        	user.setCreatedAt(LocalDateTime.now());
        }
    }
	
	@PostUpdate
	public void setUpdatedAt(Object entity) {
        if (entity instanceof User) {
        	User user = (User)entity;
        	user.setUpdatedAt(LocalDateTime.now());
        }
    }
}
