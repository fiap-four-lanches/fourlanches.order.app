package com.fiap.fourlanches.order.domain.usecases;

import com.fiap.fourlanches.order.domain.entities.Order;

public interface ValidateOrderStatusUseCase {
    void validateOrderCreated(Order order);
    void validateOrderReceived(Order order);
    void validateOrderInPreparation(Order order);
    void validateOrderReady(Order order);
    void validateOrderFinished(Order order);
    void validateOrderCanceled(Order order);
}
