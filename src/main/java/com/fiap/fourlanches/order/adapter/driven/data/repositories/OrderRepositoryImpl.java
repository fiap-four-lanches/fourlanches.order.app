package com.fiap.fourlanches.order.adapter.driven.data.repositories;

import com.fiap.fourlanches.order.adapter.driven.data.OrderJpaRepository;
import com.fiap.fourlanches.order.adapter.driven.data.entities.OrderJpaEntity;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.repositories.OrderRepository;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@AllArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private OrderJpaRepository jpaRepository;

    @Override
    public Order createOrder(Order order) {
        OrderJpaEntity orderJpaEntity = jpaRepository.save(OrderJpaEntity.fromOrder(order));
        return orderJpaEntity.toOrder();
    }

    @Override
    public Order getById(Long id) {
        OrderJpaEntity orderJpaEntity = jpaRepository.getReferenceById(id);
        return orderJpaEntity.toOrder();
    }

    @Override
    public boolean save(Order order) {
        OrderJpaEntity orderJpaEntity = OrderJpaEntity.fromOrder(order);
        jpaRepository.save(orderJpaEntity);
        return true;
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return jpaRepository.findByStatus(status.toString()).stream().map(OrderJpaEntity::toOrder).toList();
    }

    @Override
    public List<Order> getAllOrdersOrderedByStatusAndCreatedAt() {
        return jpaRepository.getAllPendingOrdersOrderedByStatusAndCreatedAt().stream().map(OrderJpaEntity::toOrder).toList();
    }

    @Override
    public void updateOrder(Long id, Order order) {
        OrderJpaEntity orderJpaEntity = jpaRepository.getReferenceById(id);
        orderJpaEntity.setStatus(order.getStatus().name());
        orderJpaEntity.setPaymentApproved(order.getPaymentApproved());
        orderJpaEntity.setUpdatedAt(LocalDateTime.now());
        jpaRepository.save(orderJpaEntity);
    }

}
