package com.ecommerce.app.controller;

import com.ecommerce.app.dto.PaymentOrderResponse;
import com.ecommerce.app.dto.PaymentVerificationRequest;
import com.ecommerce.app.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // POST /api/payment/create/{orderId}
    // Creates Razorpay order — call this before showing payment UI
    @PostMapping("/create/{orderId}")
    public ResponseEntity<PaymentOrderResponse> createPayment(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(paymentService.createPaymentOrder(orderId));
    }

    // POST /api/payment/verify
    // Call this after user completes payment
    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @Valid @RequestBody PaymentVerificationRequest request) {

        return ResponseEntity.ok(paymentService.verifyPayment(request));
    }
}