package com.fiap.fourlanches.order.application.usecases;

import com.fiap.fourlanches.order.application.dto.OrderDTO;
import com.fiap.fourlanches.order.application.exception.IncorrectOrderStatusException;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.usecases.ValidateOrderStatusUseCase;
import com.fiap.fourlanches.order.domain.valueobjects.OrderItem;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.CANCELED;
import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.CREATED;
import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.FINISHED;
import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.IN_PREPARATION;
import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.READY;
import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.RECEIVED;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ValidateOrderStatusUseCaseImplTest {

  public static final long ORDER_ID = 1234L;
  public static final BigDecimal TOTAL_PRICE = BigDecimal.valueOf(20.0);
  public static final Long CUSTOMER_ID = 5678L;

  private ValidateOrderStatusUseCase validateOrderStatusUseCase;

  @BeforeEach
  public void setUp() {
    validateOrderStatusUseCase = new ValidateOrderStatusUseCaseImpl();
  }

  @Test
  public void given_NewOrder_whenValidateOrderCreated_thenReturnUpdatedOrder() {
    Order newOrder = getOrderDTO(null).toNewOrder();

    validateOrderStatusUseCase.validateOrderCreated(newOrder);

    assertThat(newOrder.getStatus()).isEqualTo(CREATED);
  }

  @Test
  public void given_OrderCreated_whenValidateOrderReceived_thenReturnUpdatedOrder() {
    Order newOrder = getOrderDTO(CREATED).toNewOrder();

    validateOrderStatusUseCase.validateOrderReceived(newOrder);

    assertThat(newOrder.getStatus()).isEqualTo(RECEIVED);
  }

  @Test
  public void given_OrderCreated_whenValidateOrderCanceled_thenReturnUpdatedOrder() {
    Order newOrder = getOrderDTO(CREATED).toNewOrder();

    validateOrderStatusUseCase.validateOrderCanceled(newOrder);

    assertThat(newOrder.getStatus()).isEqualTo(CANCELED);
  }

  @Test
  public void given_OrderReceived_whenValidateOrderInPreparation_thenReturnUpdatedOrder() {
    Order newOrder = getOrderDTO(RECEIVED).toNewOrder();

    validateOrderStatusUseCase.validateOrderInPreparation(newOrder);

    assertThat(newOrder.getStatus()).isEqualTo(IN_PREPARATION);
  }

  @Test
  public void given_OrderInPreparation_whenValidateOrderReady_thenReturnUpdatedOrder() {
    Order newOrder = getOrderDTO(IN_PREPARATION).toNewOrder();

    validateOrderStatusUseCase.validateOrderReady(newOrder);

    assertThat(newOrder.getStatus()).isEqualTo(READY);
  }

  @Test
  public void given_OrderReady_whenValidateOrderFinished_thenReturnUpdatedOrder() {
    Order newOrder = getOrderDTO(READY).toNewOrder();

    validateOrderStatusUseCase.validateOrderFinished(newOrder);

    assertThat(newOrder.getStatus()).isEqualTo(FINISHED);
  }

  @Test
  public void given_OrderFinished_whenValidateOrderReceived_thenError() {
    Order newOrder = getOrderDTO(FINISHED).toNewOrder();

    assertThrows(IncorrectOrderStatusException.class,
            () -> validateOrderStatusUseCase.validateOrderCreated(newOrder)
    );
  }

  @Test
  public void given_OrderCanceled_whenValidateOrderReceived_thenError() {
    Order newOrder = getOrderDTO(CANCELED).toNewOrder();

    assertThrows(IncorrectOrderStatusException.class,
            () -> validateOrderStatusUseCase.validateOrderCreated(newOrder)
    );
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

}