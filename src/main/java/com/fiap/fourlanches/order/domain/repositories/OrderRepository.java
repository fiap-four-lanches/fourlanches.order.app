package com.fiap.fourlanches.order.domain.repositories;

import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.exception.OrderNotFoundException;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository {

    Order createOrder(Order order);

    Order getById(Long id) throws OrderNotFoundException;

    boolean save(Order order);

    List<Order> getOrdersByStatus(OrderStatus status);

    List<Order> getAllOrdersOrderedByStatusAndCreatedAt();

    void updateOrder(Long id, Order order);

}
