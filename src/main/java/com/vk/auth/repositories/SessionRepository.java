package com.vk.auth.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vk.auth.models.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

	Optional<Session> findByToken(String token);

	@Query("select s from Session s where s.user.id=:userId")
	List<Session> findByUserId(@Param("userId") Long userId);

}
