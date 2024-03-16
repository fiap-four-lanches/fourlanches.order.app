package com.fiap.fourlanches.order.adapter.driver.api.consumers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.fourlanches.order.application.dto.KitchenProductionQueueMessageDTO;
import com.fiap.fourlanches.order.application.dto.OrderStatusQueueMessageDTO;
import com.fiap.fourlanches.order.application.exception.FailPublishToQueueException;
import com.fiap.fourlanches.order.domain.usecases.OrderUseCase;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.fiap.fourlanches.order.application.constants.HeaderConstant.X_REQUEST_ID;

@Slf4j
@Component
@AllArgsConstructor
public class OrderStatusConsumer {
    private static final String PAYMENT_JSON_ERR_MSG = "failed to convert message to json";

    private static final String PUBLISH_ERR_MSG = "failed to publish message to queue";

    private static final String ORDER_STATUS_UPDATE_KEY = "order.status.update";

    private static String QUEUE_PAYMENT_CANCEL_NAME;

    private static String QUEUE_KITCHEN_NAME;

    @Autowired
    private OrderUseCase orderUseCase;

    @Autowired
    private AmqpTemplate queueSender;

    @Value("${queue.kitchen.name}")
    public void setQueueKitchenName(String value) {
        QUEUE_KITCHEN_NAME = value;
    }

    @Value("${queue.payment.cancel.name}")
    public void setQueuePaymentsCancelName(String value) {
        QUEUE_PAYMENT_CANCEL_NAME = value;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${queue.order.status.name}"),
            exchange = @Exchange(value = "${queue.exchange}", type = "topic"),
            key = ORDER_STATUS_UPDATE_KEY)
    )
    public void receiveMessageOrderStatus(@Payload String message,
                                          @Header(X_REQUEST_ID) String xRequestId) throws JacksonException, AmqpException {
        log.info("order status message received [x_request_id:{}][message:{}]", xRequestId, message);
        Map<String, Object> headers = new HashMap<>();
        headers.put(X_REQUEST_ID, xRequestId);
        OrderStatusQueueMessageDTO consumerMessage;

        var objMapper = new ObjectMapper();
        consumerMessage = objMapper.readValue(message, OrderStatusQueueMessageDTO.class);

        if (consumerMessage.getOrigin().equals("payment")) {
            if (consumerMessage.getStatus().equals(OrderStatus.RECEIVED)) {
                receiveMessagePaymentProcessed(consumerMessage, headers);
            }

            if (consumerMessage.getStatus().equals(OrderStatus.CANCELED)) {
                receiveMessagePaymentCanceled(consumerMessage, headers);
            }
        }

        if (consumerMessage.getOrigin().equals("kitchen")) {
            if (consumerMessage.getStatus().equals(OrderStatus.READY)) {
                receiveMessageKitchenProcessed(consumerMessage, headers);
            }

            if (consumerMessage.getStatus().equals(OrderStatus.CANCELED)) {
                receiveMessageKitchenCanceled(consumerMessage, headers);
            }
        }

        log.info("order status message consumed [x_request_id:{}][message:{}]", xRequestId, message);
    }

    public void receiveMessagePaymentProcessed(OrderStatusQueueMessageDTO consumerMessage, Map<String, Object> headers)
            throws JacksonException, AmqpException {
        log.info("order was payed successfully [x_request_id:{}][order_id:{}]",
                headers.get(X_REQUEST_ID), consumerMessage.getOrderId());

        var orderPayed = orderUseCase.receiveOrder(consumerMessage.getOrderId(), true);
        var kitchenMessage = KitchenProductionQueueMessageDTO.fromOrder(orderPayed);
        sendProductionMessageToKitchen(kitchenMessage, headers);
    }

    private void receiveMessagePaymentCanceled(OrderStatusQueueMessageDTO consumerMessage, Map<String, Object> headers) {
        log.warn("order was canceled on payment service [x_request_id:{}][order_id:{}]",
                headers.get(X_REQUEST_ID), consumerMessage.getOrderId());

        orderUseCase.orderCanceled(consumerMessage.getOrderId());
    }

    public void receiveMessageKitchenProcessed(OrderStatusQueueMessageDTO consumerMessage, Map<String, Object> headers) {
        orderUseCase.orderReady(consumerMessage.getOrderId());

        log.info("order is ready for customer to take it [x_request_id:{}][order_id:{}]",
                headers.get(X_REQUEST_ID), consumerMessage.getOrderId());
    }

    public void receiveMessageKitchenCanceled(OrderStatusQueueMessageDTO consumerMessage, Map<String, Object> headers)
            throws JacksonException, AmqpException {
        log.warn("order was canceled on kitchen service [x_request_id:{}][order_id:{}]",
                headers.get(X_REQUEST_ID), consumerMessage.getOrderId());
        orderUseCase.orderCanceled(consumerMessage.getOrderId());

        sendCancellationMessageToPayment(consumerMessage, headers);
    }

    private void sendCancellationMessageToPayment(OrderStatusQueueMessageDTO orderMessage, Map<String, Object> headers)
            throws JacksonException, AmqpException {
        var messageProperties = new MessageProperties();
        messageProperties.setHeaders(headers);
        var objectMapper = new ObjectMapper();
        var paymentMessageJson = objectMapper.writeValueAsString(orderMessage);
        var message = new Message(paymentMessageJson.getBytes(), messageProperties);
        queueSender.convertAndSend(QUEUE_PAYMENT_CANCEL_NAME, message);

    }

    private void sendProductionMessageToKitchen(KitchenProductionQueueMessageDTO orderMessage, Map<String, Object> headers)
            throws JacksonException, AmqpException {
        var messageProperties = new MessageProperties();
        messageProperties.setHeaders(headers);
        var objectMapper = new ObjectMapper();
        var paymentMessageJson = objectMapper.writeValueAsString(orderMessage);
        var message = new Message(paymentMessageJson.getBytes(), messageProperties);
        queueSender.convertAndSend(QUEUE_KITCHEN_NAME, message);
        orderUseCase.orderInPreparation(orderMessage.getOrderId());
    }

}
