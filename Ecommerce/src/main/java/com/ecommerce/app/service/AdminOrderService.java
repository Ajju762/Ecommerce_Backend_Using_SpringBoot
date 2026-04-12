package com.ecommerce.app.service;

import com.ecommerce.app.dto.OrderResponse;
import com.ecommerce.app.entity.Order;
import com.ecommerce.app.entity.OrderItem;
import com.ecommerce.app.entity.OrderStatus;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.repository.OrderRepository;
import com.ecommerce.app.dto.OrderItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderRepository orderRepository;

    // GET ALL ORDERS
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // UPDATE ORDER STATUS
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        // Validate status transition
        validateStatusTransition(order.getStatus(), status);

        order.setStatus(status);
        orderRepository.save(order);

        return mapToResponse(order);
    }

    // Status can only move forward — not backward
    // PENDING → CONFIRMED → SHIPPED → DELIVERED
    // Any status → CANCELLED
    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot change status of delivered order");
        }
        if (current == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot change status of cancelled order");
        }
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .build();
    }
}