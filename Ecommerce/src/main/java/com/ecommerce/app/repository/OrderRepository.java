package com.ecommerce.app.repository;

import com.ecommerce.app.entity.Order;
import com.ecommerce.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get all orders of a user — for order history
    List<Order> findByUserOrderByCreatedAtDesc(User user);
}