package com.fiap.fourlanches.order.adapter.driver.api.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.fourlanches.order.adapter.driver.api.controllersAdvisor.OrderControllerAdvisor;
import com.fiap.fourlanches.order.application.dto.OrderDTO;
import com.fiap.fourlanches.order.application.dto.OrderStatusQueueMessageDTO;
import com.fiap.fourlanches.order.application.dto.PaymentQueueMessageDTO;
import com.fiap.fourlanches.order.application.exception.FailPublishToQueueException;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.exception.InvalidOrderException;
import com.fiap.fourlanches.order.domain.exception.OrderNotFoundException;
import com.fiap.fourlanches.order.domain.usecases.OrderUseCase;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fiap.fourlanches.order.application.constants.HeaderConstant.X_REQUEST_ID;

@RestController
@AllArgsConstructor
@ControllerAdvice(assignableTypes = OrderControllerAdvisor.class)
@RequestMapping("orders")
@Slf4j
public class OrderController {

    private static final String PAYMENT_JSON_ERR_MSG = "failed to convert payment message to json";

    private static final String PUBLISH_ERR_MSG = "failed to publish queue message";

    private static final String CANCELLATION_JSON_ERR_MSG = "failed to convert cancellation message to json";

    private static final String CANCELLATION_ERR_MSG = "failed to publish cancellation message to queue";

    private static String QUEUE_PAYMENT_STATUS_NAME;

    private static String QUEUE_KITCHEN_STATUS_NAME;

    private OrderUseCase orderUseCase;

    private AmqpTemplate queueSender;

    @Value("${queue.payment.name}")
    public void setQueuePaymentsName(String value) {
        QUEUE_PAYMENT_STATUS_NAME = value;
    }

    @Value("${queue.kitchen.name}")
    public void setQueueKitchenName(String value) {
        QUEUE_KITCHEN_STATUS_NAME = value;
    }

    @GetMapping(value = "", produces = "application/json")
    @ApiResponse(responseCode = "200")
    public List<Order> getOrders() {
        return orderUseCase.getAllPendingOrdersOrderedByStatusAndCreatedAt();
    }

    @PostMapping(value = "", produces = "application/json")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Order> createOrder(@RequestHeader(X_REQUEST_ID) String xRequestId,
                                             @RequestBody OrderDTO orderDTO) throws InvalidOrderException {

        var orderCreated = orderUseCase.createOrder(orderDTO);

        try {
            MessageProperties messageProperties = new MessageProperties();
            Map<String, Object> headers = new HashMap<>();
            headers.put(X_REQUEST_ID, xRequestId);
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

        return ResponseEntity.status(HttpStatus.CREATED).body(orderCreated);
    }

    @PatchMapping(value = "/{orderId}/in_preparation", produces = "application/json")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> orderInPreparation(@PathVariable Long orderId)
            throws InvalidOrderException, OrderNotFoundException {
        orderUseCase.orderInPreparation(orderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{orderId}/ready", produces = "application/json")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> orderReady(@PathVariable Long orderId)
            throws InvalidOrderException {
        orderUseCase.orderReady(orderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{orderId}/finished", produces = "application/json")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> orderFinished(@PathVariable Long orderId)
            throws InvalidOrderException {
        orderUseCase.orderFinished(orderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{orderId}/cancel", produces = "application/json")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> orderCanceled(@RequestHeader(X_REQUEST_ID) String xRequestId,
                                              @PathVariable Long orderId) {
        var orderCanceled = orderUseCase.orderCanceled(orderId);

        try {
            MessageProperties messageProperties = new MessageProperties();
            Map<String, Object> headers = new HashMap<>();
            headers.put(X_REQUEST_ID, xRequestId);
            messageProperties.setHeaders(headers);
            var orderStatusMessage = OrderStatusQueueMessageDTO.fromOrder(orderCanceled);
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

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/status/{status}", produces = "application/json")
    @ApiResponse(responseCode = "200")
    public List<Order> getOrdersByStatus(@PathVariable String status) {
        var orderStatus = OrderStatus.fromString(status);
        return orderUseCase.getOrdersByStatus(orderStatus);
    }

}
