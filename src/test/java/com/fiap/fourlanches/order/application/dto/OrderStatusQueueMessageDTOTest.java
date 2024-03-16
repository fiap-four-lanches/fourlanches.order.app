package com.fiap.fourlanches.order.application.dto;

import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderStatusQueueMessageDTOTest {

    @Test
    public void givenAnOrderThenShouldReturnCorrectOrderStatusQueueMessageDTO() {
        // Arrange
        var order = Order.builder()
                .id(1L)
                .customerId(1L)
                .status(OrderStatus.CANCELED)
                .build();

        // Act
        var actualResult = OrderStatusQueueMessageDTO.fromOrder(order);

        // Assert
        assertThat(actualResult.getOrderId()).isEqualTo(1L);
        assertThat(actualResult.getCustomerId()).isEqualTo(1L);
        assertThat(actualResult.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(actualResult.getOrigin()).isEqualTo("order");
    }
    
    @Test
    public void givenAnOrderThenShouldHandleOrderWithNullValues() {
        // Arrange
        var order = new Order();
        
        // Act
        var actualResult = OrderStatusQueueMessageDTO.fromOrder(order);

        // Assert
        assertThat(actualResult.getOrderId()).isNull();
        assertThat(actualResult.getCustomerId()).isNull();
        assertThat(actualResult.getStatus()).isNull();
        assertThat(actualResult.getOrigin()).isEqualTo("order");
    }
}