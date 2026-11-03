
package com.clothstore.payment.dto;

import com.clothstore.payment.entity.PaymentMethod;
import com.clothstore.payment.entity.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private String transactionId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime paidDate;
}
