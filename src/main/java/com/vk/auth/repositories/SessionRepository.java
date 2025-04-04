package com.vk.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vk.auth.models.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long>{

}
