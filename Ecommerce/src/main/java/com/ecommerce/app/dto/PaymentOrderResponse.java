package com.ecommerce.app.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PaymentOrderResponse {
    private String razorpayOrderId;   // send to frontend
    private String currency;
    private BigDecimal amount;
    private Long orderId;             // our internal order id
}