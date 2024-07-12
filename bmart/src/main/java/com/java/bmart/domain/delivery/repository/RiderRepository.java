package com.java.bmart.domain.delivery.repository;

import com.java.bmart.domain.delivery.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Long> {

    boolean existsByUsername(String username);

    Optional<Rider> findByUsername(String username);
}
