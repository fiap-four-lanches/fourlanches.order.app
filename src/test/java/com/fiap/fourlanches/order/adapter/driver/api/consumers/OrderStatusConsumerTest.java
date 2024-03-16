package com.fiap.fourlanches.order.adapter.driver.api.consumers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.fourlanches.order.application.dto.OrderStatusQueueMessageDTO;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.usecases.OrderUseCase;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static com.fiap.fourlanches.order.application.constants.HeaderConstant.X_REQUEST_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OrderStatusConsumerTest {

    private ObjectMapper mapper;

    @Mock
    private OrderUseCase orderUseCase;

    @Mock
    private AmqpTemplate queueSender;

    @InjectMocks
    private OrderStatusConsumer consumer;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
    }

    @Test
    void receiveMessageOrderStatusShouldIgnoreMessageWhenOriginIsNotPaymentOrKitchen() throws JacksonException {
       var consumerMessage = OrderStatusQueueMessageDTO.builder()
               .orderId(1L)
               .customerId(123L)
               .status(OrderStatus.CANCELED)
               .origin("order")
               .build();

       var jsonByte = mapper.writeValueAsString(consumerMessage);
       var xRequestId = "x-test-1";

       consumer.receiveMessageOrderStatus(jsonByte, xRequestId);
    }

    @Test
    void givenMessageOrderStatusThenShouldUpdateOrderToReceivedWhenPaymentOrderIsSuccessful() throws JacksonException {
        var consumerMessage = OrderStatusQueueMessageDTO.builder()
                .orderId(1L)
                .customerId(123L)
                .status(OrderStatus.RECEIVED)
                .origin("payment")
                .build();

        var jsonByte = mapper.writeValueAsString(consumerMessage);
        var xRequestId = "x-test-2";

        var wantedReceivedOrder = Order.builder()
                .id(1L)
                .customerId(123L)
                .status(OrderStatus.RECEIVED)
                .build();
        when(orderUseCase.receiveOrder(anyLong(), eq(true))).thenReturn(wantedReceivedOrder);

        consumer.receiveMessageOrderStatus(jsonByte, xRequestId);

        verify(orderUseCase).orderInPreparation(eq(1L));
    }

    @Test
    void givenMessageOrderStatusThenShouldUpdateOrderToCanceledWhenPaymentOrderIsCanceled() throws JacksonException {
        var consumerMessage = OrderStatusQueueMessageDTO.builder()
                .orderId(1L)
                .customerId(123L)
                .status(OrderStatus.CANCELED)
                .origin("payment")
                .build();

        var jsonByte = mapper.writeValueAsString(consumerMessage);
        var xRequestId = "x-test-3";

        consumer.receiveMessageOrderStatus(jsonByte, xRequestId);

        verify(orderUseCase).orderCanceled(eq(1L));
    }

    @Test
    void givenMessageOrderStatusThenShouldUpdateOrderToReadyWhenKitchenOrderIsSuccessful() throws JacksonException {
        var consumerMessage = OrderStatusQueueMessageDTO.builder()
                .orderId(1L)
                .customerId(123L)
                .status(OrderStatus.READY)
                .origin("kitchen")
                .build();

        var jsonByte = mapper.writeValueAsString(consumerMessage);
        var xRequestId = "x-test-4";

        consumer.receiveMessageOrderStatus(jsonByte, xRequestId);

        verify(orderUseCase).orderReady(eq(1L));
    }

    @Test
    void givenMessageOrderStatusThenShouldUpdateOrderToCanceledWhenKitchenOrderIsSuccessful() throws JacksonException {
        var consumerMessage = OrderStatusQueueMessageDTO.builder()
                .orderId(1L)
                .customerId(123L)
                .status(OrderStatus.CANCELED)
                .origin("kitchen")
                .build();

        var jsonByte = mapper.writeValueAsString(consumerMessage);
        var xRequestId = "x-test-5";
        var messageProperties = new MessageProperties();
        Map<String, Object> headers = new HashMap<>();
        headers.put(X_REQUEST_ID, xRequestId);
        messageProperties.setHeaders(headers);
        var message = new Message(jsonByte.getBytes(), messageProperties);

        consumer.receiveMessageOrderStatus(jsonByte, xRequestId);

        verify(orderUseCase).orderCanceled(eq(1L));
        verify(queueSender).convertAndSend(eq(null), eq(message));
    }
}