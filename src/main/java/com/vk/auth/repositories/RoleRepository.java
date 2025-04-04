package com.vk.auth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vk.auth.models.UserRole;

@Repository
public interface RoleRepository extends JpaRepository<UserRole, Long>{
	
	Optional<UserRole> findByName(String name);

}
