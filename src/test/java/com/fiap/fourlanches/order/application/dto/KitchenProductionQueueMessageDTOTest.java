package com.fiap.fourlanches.order.application.dto;

import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.valueobjects.OrderItem;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KitchenProductionQueueMessageDTOTest {

    // Test for fromOrder method 
    @Test
    void givenAnAnOrderThenShouldGenerateDTOFromOrder() {
        // Arrange
        List<OrderItem> items = new ArrayList<>();

        var order = Order.builder()
                .id(1L)
                .orderItems(items)
                .status(OrderStatus.IN_PREPARATION)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        // Act
        var result = KitchenProductionQueueMessageDTO.fromOrder(order);

        // Assert
        assertEquals(result.getId(), order.getId());
        assertEquals(result.getOrderId(), order.getId());
        assertEquals(result.getOrderItems(), order.getOrderItems());
        assertEquals(result.getStatus(), order.getStatus());
        assertEquals(result.getCreatedAt(), order.getCreatedAt());
        assertEquals(result.getUpdatedAt(), order.getUpdatedAt());
    }

}