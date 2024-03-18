package com.fiap.fourlanches.order.application.usecases;

import com.fiap.fourlanches.order.application.dto.OrderDTO;
import com.fiap.fourlanches.order.application.exception.FailPublishToQueueException;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.exception.InvalidOrderException;
import com.fiap.fourlanches.order.domain.repositories.OrderRepository;
import com.fiap.fourlanches.order.domain.usecases.OrderUseCase;
import com.fiap.fourlanches.order.domain.usecases.ProductUseCase;
import com.fiap.fourlanches.order.domain.usecases.ValidateOrderStatusUseCase;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.*;

@Slf4j
@Service
@AllArgsConstructor
public class OrderUseCaseImpl implements OrderUseCase {


    private final OrderRepository repository;
    private final ValidateOrderStatusUseCase validateOrderStatusUseCase;
    private final ProductUseCase productUseCase;

    @Override
    public List<Order> getAllPendingOrdersOrderedByStatusAndCreatedAt() {
        return repository.getAllOrdersOrderedByStatusAndCreatedAt();
    }

    @Override
    public Order createOrder(OrderDTO orderDTO) throws InvalidOrderException, FailPublishToQueueException {
        Order order = orderDTO.toNewOrder();

        delegateOrderStatusValidation(order, OrderDTO.builder().status(CREATED).build());

        setOrderItemPrices(order);
        order.setTotalPrice(order.calculateTotalPrice());

        if (!order.isValid()) {
            throw new InvalidOrderException();
        }

        return repository.createOrder(order);
    }

    public Order receiveOrder(Long orderId, boolean paymentApproved) {
        Order order = repository.getById(orderId);
        delegateOrderStatusValidation(order, OrderDTO.builder().status(RECEIVED).build());
        order.setPaymentApproved(paymentApproved);
        repository.updateOrder(orderId, order);
        return order;
    }

    @Override
    public void orderInPreparation(Long orderId) {
        updateOrderStatus(orderId, IN_PREPARATION);
    }

    @Override
    public void orderReady(Long orderId) {
        updateOrderStatus(orderId, READY);
    }

    @Override
    public void orderFinished(Long orderId) {
        updateOrderStatus(orderId, FINISHED);
    }

    @Override
    public Order orderCanceled(Long orderId) {
        return updateOrderStatus(orderId, CANCELED);

    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return repository.getOrdersByStatus(status);
    }

    private Order updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        var order = repository.getById(orderId);
        delegateOrderStatusValidation(order, OrderDTO.builder().status(orderStatus).build());
        repository.updateOrder(orderId, order);
        return order;
    }

    private void delegateOrderStatusValidation(Order order, OrderDTO orderDTO) {
        if (!ObjectUtils.isEmpty(orderDTO.getStatus())) {
            switch (orderDTO.getStatus()) {
                case CREATED -> validateOrderStatusUseCase.validateOrderCreated(order);
                case RECEIVED -> validateOrderStatusUseCase.validateOrderReceived(order);
                case IN_PREPARATION -> validateOrderStatusUseCase.validateOrderInPreparation(order);
                case READY -> validateOrderStatusUseCase.validateOrderReady(order);
                case FINISHED -> validateOrderStatusUseCase.validateOrderFinished(order);
                case CANCELED -> validateOrderStatusUseCase.validateOrderCanceled(order);
            }
            order.setStatus(orderDTO.getStatus());
        }
    }

    private void setOrderItemPrices(Order order) {
        order.getOrderItems().forEach(item ->
                item.setPrice(productUseCase.getProductById(item.getProductId()).getPrice().doubleValue()));
    }
}
