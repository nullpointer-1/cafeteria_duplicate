package com.example.shopmanagement.repository;

import com.example.shopmanagement.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findByVendorId(Long vendorId);
    List<Shop> findByActive(boolean active);
}