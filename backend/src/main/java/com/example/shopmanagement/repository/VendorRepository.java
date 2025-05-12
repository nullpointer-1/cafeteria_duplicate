package com.example.shopmanagement.repository;

import com.example.shopmanagement.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUsername(String username);
    Optional<Vendor> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}