package com.fiap.fourlanches.order.application.dto;

import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusQueueMessageDTO implements Serializable {
    private Long orderId;
    private Long customerId;
    private OrderStatus status;
    private String origin;

    public static OrderStatusQueueMessageDTO fromOrder(Order order) {
        return OrderStatusQueueMessageDTO.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .origin("order")
                .build();
    }
}
