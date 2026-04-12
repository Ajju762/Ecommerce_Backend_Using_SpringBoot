package com.ecommerce.app.repository;


import com.ecommerce.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring Data JPA generates the SQL automatically from method name
    List<Product> findByCategory(String category);

    List<Product> findByNameContainingIgnoreCase(String name);
    
    
    // Pagination support
    Page<Product> findAll(Pageable pageable);

    // Search by name with pagination
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Filter by category with pagination
    Page<Product> findByCategory(String category, Pageable pageable);

    // Search by name AND category
    Page<Product> findByNameContainingIgnoreCaseAndCategory(
            String name, String category, Pageable pageable);
}