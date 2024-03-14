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
    private Long orderId;
    private Long customerId;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private Boolean paymentApproved;

    public static PaymentQueueMessageDTO fromOrder(Order order) {
        return PaymentQueueMessageDTO.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .paymentApproved(order.getPaymentApproved())
                .build();
    }
}
