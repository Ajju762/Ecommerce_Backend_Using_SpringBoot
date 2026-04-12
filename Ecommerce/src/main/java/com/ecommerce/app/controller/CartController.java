package com.ecommerce.app.controller;

import com.ecommerce.app.dto.CartItemRequest;
import com.ecommerce.app.dto.CartResponse;
import com.ecommerce.app.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // GET /api/cart
    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    // POST /api/cart
    @PostMapping
    public ResponseEntity<CartResponse> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CartItemRequest request) {

        return ResponseEntity.ok(
                cartService.addToCart(userDetails.getUsername(), request));
    }

    // PUT /api/cart/{cartItemId}
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                cartService.updateQuantity(userDetails.getUsername(), cartItemId, quantity));
    }

    // DELETE /api/cart/{cartItemId}
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId) {

        return ResponseEntity.ok(
                cartService.removeItem(userDetails.getUsername(), cartItemId));
    }

    // DELETE /api/cart
    @DeleteMapping
    public ResponseEntity<String> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {

        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.ok("Cart cleared successfully");
    }
}