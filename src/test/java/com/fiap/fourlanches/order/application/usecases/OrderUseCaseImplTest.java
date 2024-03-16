package com.fiap.fourlanches.order.application.usecases;

import com.fiap.fourlanches.order.application.dto.OrderDTO;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.exception.InvalidOrderException;
import com.fiap.fourlanches.order.domain.repositories.OrderRepository;
import com.fiap.fourlanches.order.domain.usecases.OrderUseCase;
import com.fiap.fourlanches.order.domain.usecases.ProductUseCase;
import com.fiap.fourlanches.order.domain.usecases.ValidateOrderStatusUseCase;
import com.fiap.fourlanches.order.domain.valueobjects.OrderItem;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;

import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.CREATED;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestConfiguration.class)
class OrderUseCaseImplTest {

    private static final long ORDER_ID = 1234L;
    private static final BigDecimal TOTAL_PRICE = BigDecimal.valueOf(20.0);
    private static final Long CUSTOMER_ID = 5678L;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ValidateOrderStatusUseCase validateOrderStatusUseCase;

    @Mock
    private ProductUseCase productUseCase;

    private OrderUseCase orderUseCase;

    @BeforeEach
    void setUp() {
        orderUseCase = new OrderUseCaseImpl(orderRepository, validateOrderStatusUseCase, productUseCase);
    }

    @Test
    void whenGetAllPendingOrdersOrderedByStatusAndCreatedAt_thenReturnProduct() {
        Order order = getOrderDTO().toNewOrder();
        when(orderRepository.getAllOrdersOrderedByStatusAndCreatedAt()).thenReturn(singletonList(order));

        List<Order> orders = orderUseCase.getAllPendingOrdersOrderedByStatusAndCreatedAt();

        assertThat(orders).contains(order);
    }

    @Test
    void givenOrderToBeCreated_whenCreateFails_ThenError() {
        assertThrows(InvalidOrderException.class,
                () -> orderUseCase.createOrder(getInvalidOrderDTO()));
    }

    @Test
    void givenId_whenReceiveOrder_thenChangeStatusAndPaymentApproved() {
        Order order = getOrderDTO(CREATED).toNewOrder();
        when(orderRepository.getById(eq(ORDER_ID))).thenReturn(order);

        orderUseCase.receiveOrder(ORDER_ID, true);

        order.setPaymentApproved(true);
        verify(orderRepository).updateOrder(eq(ORDER_ID), eq(order));
    }

    @Test
    void givenId_whenOrderInPreparation_thenUpdateOrder() {
        Order order = getOrderDTO(CREATED).toNewOrder();
        when(orderRepository.getById(eq(ORDER_ID))).thenReturn(order);

        orderUseCase.orderInPreparation(ORDER_ID);

        verify(orderRepository).updateOrder(eq(ORDER_ID), eq(order));
    }

    @Test
    void givenId_whenOrderReady_thenUpdateOrder() {
        Order order = getOrderDTO(CREATED).toNewOrder();
        when(orderRepository.getById(eq(ORDER_ID))).thenReturn(order);

        orderUseCase.orderReady(ORDER_ID);

        verify(orderRepository).updateOrder(eq(ORDER_ID), eq(order));
    }

    @Test
    void givenId_whenOrderFinished_thenUpdateOrder() {
        Order order = getOrderDTO(CREATED).toNewOrder();
        when(orderRepository.getById(eq(ORDER_ID))).thenReturn(order);

        orderUseCase.orderFinished(ORDER_ID);

        verify(orderRepository).updateOrder(eq(ORDER_ID), eq(order));
    }

    @Test
    void givenId_whenOrderCanceled_thenUpdateOrder() {
        Order order = getOrderDTO(CREATED).toNewOrder();
        when(orderRepository.getById(eq(ORDER_ID))).thenReturn(order);

        orderUseCase.orderCanceled(ORDER_ID);

        verify(orderRepository).updateOrder(eq(ORDER_ID), eq(order));
    }

    @Test
    void whenGetOrdersByStatus_thenReturnProduct() {
        Order order = getOrderDTO().toNewOrder();
        when(orderRepository.getOrdersByStatus(CREATED)).thenReturn(singletonList(order));

        List<Order> orders = orderUseCase.getOrdersByStatus(CREATED);

        assertThat(orders).contains(order);
    }

    private static OrderDTO getOrderDTO() {
        return getOrderDTO(CREATED);
    }

    private static OrderDTO getOrderDTO(OrderStatus status) {
        return OrderDTO.builder()
                .orderItems(singletonList(OrderItem.builder()
                        .productId(ORDER_ID)
                        .quantity(10)
                        .price(2.0)
                        .build()))
                .customerId(CUSTOMER_ID)
                .totalPrice(TOTAL_PRICE)
                .status(status)
                .paymentApproved(false)
                .build();
    }

    private OrderDTO getInvalidOrderDTO() {
        return OrderDTO.builder()
                .orderItems(emptyList())
                .customerId(CUSTOMER_ID)
                .paymentApproved(false)
                .build();
    }

}