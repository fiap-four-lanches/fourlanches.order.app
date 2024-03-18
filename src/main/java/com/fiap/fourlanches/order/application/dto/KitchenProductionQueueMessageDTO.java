package com.fiap.fourlanches.order.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.valueobjects.OrderItem;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class KitchenProductionQueueMessageDTO {
    private Long id;
    private Long orderId;
    private List<OrderItem> orderItems;
    private OrderStatus status;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;


    public static KitchenProductionQueueMessageDTO fromOrder(Order order) {
        return KitchenProductionQueueMessageDTO.builder()
                .id(order.getId())
                .orderId(order.getId())
                .orderItems(order.getOrderItems())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
