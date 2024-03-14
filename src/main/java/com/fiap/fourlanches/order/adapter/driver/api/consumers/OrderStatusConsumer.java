package com.fiap.fourlanches.order.adapter.driver.api.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.fourlanches.order.application.dto.OrderStatusQueueMessageDTO;
import com.fiap.fourlanches.order.application.exception.FailPublishToQueueException;
import com.fiap.fourlanches.order.application.exception.IncorrectOrderStatusException;
import com.fiap.fourlanches.order.domain.usecases.OrderUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.fiap.fourlanches.order.application.constants.HeaderConstant.X_REQUEST_ID;

@Slf4j
@Component
public class OrderStatusConsumer {
    private static final String PAYMENT_JSON_ERR_MSG = "failed to convert message to json";

    private static final String PUBLISH_ERR_MSG = "failed to publish message to queue";

    @Autowired
    private OrderUseCase orderUseCase;

    @Autowired
    private AmqpTemplate queueSender;

    @Value("${queue.payment.name}")
    String QUEUE_PAYMENT_STATUS_NAME;

    @Value("${queue.kitchen.name}")
    String QUEUE_KITCHEN_STATUS_NAME;

    @Value("")
    String QUEUE_ORDER_STATUS_NAME;

    @RabbitListener(queues = "${queue.order.status.name}")
    public void receiveMessage(@Payload String message, @Header(X_REQUEST_ID) String xRequestId) {
        log.info("message received [x_request_id:{}][message:{}]", xRequestId, message);
        Map<String, Object> headers = new HashMap<>();
        headers.put(X_REQUEST_ID, xRequestId);
        OrderStatusQueueMessageDTO consumerMessage;
       try {
           var objMapper = new ObjectMapper();
           consumerMessage = objMapper.readValue(message, OrderStatusQueueMessageDTO.class);
       } catch (JsonProcessingException e) {
           log.error("could not read message", e);
           throw new RuntimeException(e);
       }
        switch (consumerMessage.getStatus()) {
            case CANCELED:
                try {
                    orderUseCase.orderCanceled(consumerMessage.getOrderId());
                    if (consumerMessage.getOrigin().equals("payment")) {
                        log.warn("order was canceled on payment service [x_request_id:{}][order_id:{}]", xRequestId, consumerMessage.getOrderId());
                        // we must do nothing here, as the order was canceled and payment is the first service on the saga after order service
                    }
                    if (consumerMessage.getOrigin().equals("kitchen")) {
                        log.warn("order was canceled on kitchen service [x_request_id:{}][order_id:{}]", xRequestId, consumerMessage.getOrderId());
                        this.sendCancellationMessageToPayment(consumerMessage, headers);
                    }
                } catch (IncorrectOrderStatusException e) {

                }
                break;
            case RECEIVED:
                log.debug("order was payed successfully [x_request_id:{}][order_id:{}]", xRequestId, consumerMessage.getOrderId());
                orderUseCase.receiveOrder(consumerMessage.getOrderId(), true);
                this.sendProductionMessageToKitchen(consumerMessage, headers);
                break;
            case READY:
                orderUseCase.orderReady(consumerMessage.getCustomerId());
                log.debug("order is ready for customer to take it [x_request_id:{}][order_id:{}]", xRequestId, consumerMessage.getOrderId());
                break;
            default:
                log.warn("invalid status message [x_request_id:{}][order_id:{}]", xRequestId, consumerMessage.getOrderId());
                break;
        }
        log.info("message consumed [x_request_id:{}][message:{}]", xRequestId, message);
    }

    private void sendCancellationMessageToPayment(OrderStatusQueueMessageDTO orderMessage, Map<String, Object> headers) {
        this.sendCancellationMessage(orderMessage, headers, QUEUE_PAYMENT_STATUS_NAME);
    }

    private void sendCancellationMessageToKitchen(OrderStatusQueueMessageDTO orderMessage, Map<String, Object> headers) {
        this.sendCancellationMessage(orderMessage, headers, QUEUE_KITCHEN_STATUS_NAME);
    }

    private void sendCancellationMessage(OrderStatusQueueMessageDTO orderMessage, Map<String, Object> headers, String queueName) {
        try {
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setHeaders(headers);
            var objectMapper = new ObjectMapper();
            var paymentMessageJson = objectMapper.writeValueAsString(orderMessage);
            var message = new Message(paymentMessageJson.getBytes(), messageProperties);
            queueSender.convertAndSend(queueName, message);
        } catch (JsonProcessingException e) {
            log.error(PAYMENT_JSON_ERR_MSG, e);
            throw new FailPublishToQueueException(PAYMENT_JSON_ERR_MSG, e);
        } catch (AmqpException e) {
            log.error(PUBLISH_ERR_MSG, e);
            throw new FailPublishToQueueException(PUBLISH_ERR_MSG, e);
        }
    }

    private void sendProductionMessageToKitchen(OrderStatusQueueMessageDTO orderMessage, Map<String, Object> headers) {
        this.sendProductionMessage(orderMessage, headers, QUEUE_KITCHEN_STATUS_NAME);
    }

    private void sendProductionMessage(OrderStatusQueueMessageDTO orderMessage, Map<String, Object> headers, String queueName) {
        // TODO: improve here
    }


}
