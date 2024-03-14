package com.fiap.fourlanches.order.domain.usecases;

import com.fiap.fourlanches.order.application.dto.OrderDTO;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;

import java.util.List;
import java.util.Map;

public interface OrderUseCase {

    List<Order> getAllPendingOrdersOrderedByStatusAndCreatedAt();
    List<Order> getOrdersByStatus(OrderStatus status);
    Long createOrder(OrderDTO orderDTO, Map<String, Object> headers);
    void receiveOrder(Long orderId, boolean paymentApproved);
    void orderInPreparation(Long orderId);
    void orderReady(Long orderId);
    void orderFinished(Long orderId);
    void orderCanceled(Long orderId);

}
