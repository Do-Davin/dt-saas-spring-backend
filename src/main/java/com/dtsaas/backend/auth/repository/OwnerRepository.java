package com.dtsaas.backend.auth.repository;

import com.dtsaas.backend.auth.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {

    Optional<Owner> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Owner> findByUsername(String username);

    boolean existsByUsername(String username);
}
