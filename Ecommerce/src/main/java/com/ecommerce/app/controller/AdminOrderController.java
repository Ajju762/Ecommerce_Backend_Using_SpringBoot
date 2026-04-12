package com.ecommerce.app.controller;

import com.ecommerce.app.dto.OrderResponse;
import com.ecommerce.app.entity.OrderStatus;
import com.ecommerce.app.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")  // entire controller is admin only
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    // GET /api/admin/orders — all orders in system
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(adminOrderService.getAllOrders());
    }

    // PUT /api/admin/orders/{id}/status?status=SHIPPED
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(adminOrderService.updateOrderStatus(id, status));
    }
}