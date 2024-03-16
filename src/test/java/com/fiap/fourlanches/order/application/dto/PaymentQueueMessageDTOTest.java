package com.fiap.fourlanches.order.application.dto;

import com.fiap.fourlanches.order.domain.entities.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PaymentQueueMessageDTOTest {

    @Test
    void givenAnOrderThenShouldCreateDTOFromOrder() {
        var order = Order.builder()
                .customerId(1L)
                .totalPrice(BigDecimal.valueOf(100))
                .build();

        var dto = PaymentQueueMessageDTO.fromOrder(order);

        assertEquals(order.getCustomerId(), dto.getCustomerId());
        assertEquals(order.getTotalPrice(), dto.getOrder().getTotalPrice());
    }
}