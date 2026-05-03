package com.tranquiloos.users.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUserEntity, Long> {

	Optional<AppUserEntity> findByEmail(String email);

	Optional<AppUserEntity> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);
}
