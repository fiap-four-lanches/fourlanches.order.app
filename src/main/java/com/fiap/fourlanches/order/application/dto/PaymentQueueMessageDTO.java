package com.fiap.fourlanches.order.application.dto;

import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentQueueMessageDTO {
    private Long customerId;
    private PaymentOrderResumeDTO order;

    public static PaymentQueueMessageDTO fromOrder(Order order) {
        var orderResume = PaymentOrderResumeDTO.builder()
                .id(order.getId())
                .description("")
                .totalPrice(order.getTotalPrice())
                .build();
        return PaymentQueueMessageDTO.builder()
                .customerId(order.getCustomerId())
                .order(orderResume)
                .build();
    }
}
