package com.fiap.fourlanches.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResumeDTO {
    private Long id;
    private BigDecimal totalPrice;
    private String description;
}
