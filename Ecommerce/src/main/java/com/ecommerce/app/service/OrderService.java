package com.ecommerce.app.service;

import com.ecommerce.app.dto.OrderRequest;
import com.ecommerce.app.dto.OrderItemResponse;
import com.ecommerce.app.dto.OrderResponse;
import com.ecommerce.app.entity.*;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    // -------------------------------------------------------
    // PLACE ORDER — convert cart to order
    // -------------------------------------------------------
    public OrderResponse placeOrder(String email, OrderRequest request) {
        User user = getUser(email);

        // Get user's cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot place order with empty cart");
        }

        // Build order items from cart items
        // snapshot price is copied here
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getProduct().getPrice()) // snapshot
                        .build())
                .toList();

        // Calculate total
        BigDecimal total = orderItems.stream()
                .map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = Order.builder()
                .user(user)
                .items(orderItems)
                .status(OrderStatus.PENDING)
                .totalAmount(total)
                .deliveryAddress(request.getDeliveryAddress())
                .build();

        // Link each item back to order
        orderItems.forEach(item -> item.setOrder(order));

        orderRepository.save(order);

        // Clear cart after order placed
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToResponse(order);
    }

    // -------------------------------------------------------
    // GET MY ORDERS — order history
    // -------------------------------------------------------
    public List<OrderResponse> getMyOrders(String email) {
        User user = getUser(email);
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // -------------------------------------------------------
    // GET ORDER BY ID
    // -------------------------------------------------------
    public OrderResponse getOrderById(String email, Long orderId) {
        User user = getUser(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        // Security check — user can only see their own orders
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return mapToResponse(order);
    }

    // -------------------------------------------------------
    // CANCEL ORDER
    // -------------------------------------------------------
    public OrderResponse cancelOrder(String email, Long orderId) {
        User user = getUser(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Can only cancel PENDING orders
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException(
                "Cannot cancel order in " + order.getStatus() + " status");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return mapToResponse(order);
    }

    // -------------------------------------------------------
    // PRIVATE HELPERS
    // -------------------------------------------------------
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", 0L));
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