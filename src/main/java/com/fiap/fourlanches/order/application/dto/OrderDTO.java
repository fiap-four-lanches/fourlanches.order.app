package com.fiap.fourlanches.order.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.valueobjects.OrderItem;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @JsonProperty("items")
    private List<OrderItem> orderItems;
    private Long customerId;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private Boolean paymentApproved;

    public Order toNewOrder() {
        return Order.builder()
                .orderItems(orderItems)
                .customerId(customerId)
                .totalPrice(totalPrice)
                .status(status)
                .paymentApproved(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
