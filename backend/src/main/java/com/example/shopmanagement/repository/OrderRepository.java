package com.example.shopmanagement.repository;

import com.example.shopmanagement.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByShopId(Long shopId);
    List<Order> findByUserId(Long userId);
    Optional<Order> findByOrderId(String orderId);
}