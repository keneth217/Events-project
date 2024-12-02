package com.example.events.payments;

import lombok.*;

import java.math.BigDecimal;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PaymentRequest {
    private String email;
        private BigDecimal amount;
        private String callbackUrl;
}
