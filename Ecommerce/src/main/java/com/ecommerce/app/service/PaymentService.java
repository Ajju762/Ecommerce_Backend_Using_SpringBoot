package com.ecommerce.app.service;

import com.ecommerce.app.dto.PaymentOrderResponse;
import com.ecommerce.app.dto.PaymentVerificationRequest;
import com.ecommerce.app.entity.Order;
import com.ecommerce.app.entity.OrderStatus;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.repository.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;

    @Value("${app.razorpay.key-secret}")
    private String keySecret;

    // -------------------------------------------------------
    // CREATE RAZORPAY ORDER
    // Called when user clicks "Pay Now"
    // -------------------------------------------------------
    public PaymentOrderResponse createPaymentOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        try {
            // Razorpay takes amount in PAISE (1 INR = 100 paise)
            int amountInPaise = order.getTotalAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .intValue();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_" + orderId);

            com.razorpay.Order razorpayOrder =
                    razorpayClient.orders.create(orderRequest);

            log.info("Razorpay order created: {}", (Object) razorpayOrder.get("id"));

            return PaymentOrderResponse.builder()
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .currency("INR")
                    .amount(order.getTotalAmount())
                    .orderId(orderId)
                    .build();

        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed: {}", e.getMessage());
            throw new RuntimeException("Payment initiation failed: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // VERIFY PAYMENT
    // Called after user completes payment on frontend
    // Razorpay sends 3 values — we verify signature
    // -------------------------------------------------------
    public String verifyPayment(PaymentVerificationRequest request) {
        try {
            // Signature verification — prevents fraud
            // Razorpay signs: orderId + "|" + paymentId with your secret
            String payload = request.getRazorpayOrderId()
                    + "|" + request.getRazorpayPaymentId();

            String generatedSignature = hmacSha256(payload, keySecret);

            if (generatedSignature.equals(request.getRazorpaySignature())) {
                // Payment is genuine — update order status
                Order order = orderRepository.findById(request.getOrderId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Order", request.getOrderId()));

                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);

                log.info("Payment verified for order: {}", (Object) request.getOrderId());                
                return "Payment verified successfully";

            } else {
                log.warn("Invalid payment signature for order: {}",
                        request.getOrderId());
                throw new RuntimeException("Invalid payment signature");
            }

        } catch (Exception e) {
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // HMAC-SHA256 — signature generation
    // -------------------------------------------------------
    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // Convert to hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}