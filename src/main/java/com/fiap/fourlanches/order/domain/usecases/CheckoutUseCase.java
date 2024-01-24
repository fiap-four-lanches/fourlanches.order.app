package com.fiap.fourlanches.order.domain.usecases;

import com.fiap.fourlanches.order.application.dto.OrderDTO;

public interface CheckoutUseCase {
    Long processPayment(OrderDTO orderDTO);
}
