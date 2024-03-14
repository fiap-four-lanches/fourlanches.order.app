package com.fiap.fourlanches.order.application.usecases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.fourlanches.order.application.dto.OrderDTO;
import com.fiap.fourlanches.order.application.dto.OrderStatusQueueMessageDTO;
import com.fiap.fourlanches.order.application.dto.PaymentQueueMessageDTO;
import com.fiap.fourlanches.order.application.exception.FailPublishToQueueException;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.exception.InvalidOrderException;
import com.fiap.fourlanches.order.domain.repositories.OrderRepository;
import com.fiap.fourlanches.order.domain.usecases.ValidateOrderStatusUseCase;
import com.fiap.fourlanches.order.domain.usecases.OrderUseCase;
import com.fiap.fourlanches.order.domain.usecases.ProductUseCase;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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


    private static final String PAYMENT_JSON_ERR_MSG = "failed to convert payment message to json";
    private static final String PUBLISH_ERR_MSG = "failed to publish queue message";

    private static final String CANCELLATION_JSON_ERR_MSG = "failed to convert cancellation message to json";
    private static final String CANCELLATION_ERR_MSG = "failed to publish cancellation message to queue";

    private final OrderRepository repository;
    private final ValidateOrderStatusUseCase validateOrderStatusUseCase;
    private final ProductUseCase productUseCase;
    private final AmqpTemplate queueSender;

    private static String QUEUE_PAYMENT_STATUS_NAME;

    private static String QUEUE_KITCHEN_STATUS_NAME;

    @Value("${queue.payment.name}")
    public void setQueuePaymentsName(String value) {
        this.QUEUE_PAYMENT_STATUS_NAME = value;
    }
    @Value("${queue.kitchen.name}")
    public void setQueueKitchenName(String value) {
        this.QUEUE_KITCHEN_STATUS_NAME = value;
    }

    @Override
    public List<Order> getAllPendingOrdersOrderedByStatusAndCreatedAt() {
        return repository.getAllOrdersOrderedByStatusAndCreatedAt();
    }

    @Override
    public Long createOrder(OrderDTO orderDTO, Map<String, Object> headers) throws InvalidOrderException, FailPublishToQueueException {
        Order order = orderDTO.toNewOrder();

        delegateOrderStatusValidation(order, OrderDTO.builder().status(CREATED).build());

        setOrderItemPrices(order);
        order.setTotalPrice(order.calculateTotalPrice());

        if (!order.isValid()) {
            throw new InvalidOrderException();
        }

        var orderCreated = repository.createOrder(order);

        try {
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setHeaders(headers);
            var paymentMessage = PaymentQueueMessageDTO.fromOrder(orderCreated);
            var objectMapper = new ObjectMapper();
            var paymentMessageJson = objectMapper.writeValueAsString(paymentMessage);
            var message = new Message(paymentMessageJson.getBytes(), messageProperties);
            queueSender.convertAndSend(QUEUE_PAYMENT_STATUS_NAME, message);
        } catch (JsonProcessingException e) {
            log.error(PAYMENT_JSON_ERR_MSG, e);
            throw new FailPublishToQueueException(PAYMENT_JSON_ERR_MSG, e);
        } catch (AmqpException e) {
            log.error(PUBLISH_ERR_MSG, e);
            throw new FailPublishToQueueException(PUBLISH_ERR_MSG, e);
        }


        return orderCreated.getId();
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
        var oderCanceled = updateOrderStatus(orderId, CANCELED);
        try {
            MessageProperties messageProperties = new MessageProperties();
            // TODO: messageProperties.setHeaders(headers);
            var orderStatusMessage = OrderStatusQueueMessageDTO.fromOrder(oderCanceled);
            var objectMapper = new ObjectMapper();
            var paymentMessageJson = objectMapper.writeValueAsString(orderStatusMessage);
            var message = new Message(paymentMessageJson.getBytes(), messageProperties);
            queueSender.convertAndSend(QUEUE_PAYMENT_STATUS_NAME, message);
            queueSender.convertAndSend(QUEUE_KITCHEN_STATUS_NAME, message);
        } catch (JsonProcessingException e) {
            log.error(CANCELLATION_JSON_ERR_MSG, e);
            throw new FailPublishToQueueException(CANCELLATION_JSON_ERR_MSG, e);
        } catch (AmqpException e) {
            log.error(CANCELLATION_ERR_MSG, e);
            throw new FailPublishToQueueException(CANCELLATION_ERR_MSG, e);
        }
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return repository.getOrdersByStatus(status);
    }

    private Order updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        var order = repository.getById(orderId);
        order.toString();
        var dto = OrderDTO.builder().status(orderStatus).build();
        delegateOrderStatusValidation(order, dto);
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
