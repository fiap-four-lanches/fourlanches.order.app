package com.fiap.fourlanches.order.application.usecases;

import com.fiap.fourlanches.order.application.dto.OrderDTO;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.entities.Product;
import com.fiap.fourlanches.order.domain.exception.InvalidOrderException;
import com.fiap.fourlanches.order.domain.repositories.OrderRepository;
import com.fiap.fourlanches.order.domain.usecases.ValidateOrderStatusUseCase;
import com.fiap.fourlanches.order.domain.usecases.OrderUseCase;
import com.fiap.fourlanches.order.domain.usecases.ProductUseCase;
import com.fiap.fourlanches.order.domain.valueobjects.OrderItem;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.CANCELED;
import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.CREATED;
import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.FINISHED;
import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.IN_PREPARATION;
import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.READY;
import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.RECEIVED;

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
    public Long createOrder(OrderDTO orderDTO) throws InvalidOrderException {
        Order order = orderDTO.toNewOrder();

        delegateOrderStatusValidation(order, OrderDTO.builder().status(CREATED).build());

        setOrderItemPrices(order);
        order.setTotalPrice(order.calculateTotalPrice());

        if(!order.isValid()) {
            throw new InvalidOrderException();
        }

        return repository.createOrder(order).getId();
    }

    public void receiveOrder(Long orderId, boolean paymentApproved) {
        Order order = repository.getById(orderId);
        delegateOrderStatusValidation(order, OrderDTO.builder().status(RECEIVED).build());
        order.setPaymentApproved(paymentApproved);
        repository.updateOrder(orderId, order);
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
    public void orderCanceled(Long orderId) {
        updateOrderStatus(orderId, CANCELED);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return repository.getOrdersByStatus(status);
    }

    private void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = repository.getById(orderId);
        delegateOrderStatusValidation(order, OrderDTO.builder().status(orderStatus).build());
        repository.updateOrder(orderId, order);
    }

    private void delegateOrderStatusValidation(Order order, OrderDTO orderDTO) {
        if(!ObjectUtils.isEmpty(orderDTO.getStatus())) {
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
