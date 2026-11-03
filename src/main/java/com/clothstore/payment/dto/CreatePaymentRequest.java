
package com.clothstore.payment.dto;

import com.clothstore.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequest {

    @NotNull
    private PaymentMethod paymentMethod;
}
