package com.fiap.fourlanches.order.adapter.driven.data.entities;

import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;

public class OrderJpaEntityTest {

  public static final Long ORDER_ID = 1234L;
  public static final BigDecimal TOTAL_PRICE = BigDecimal.valueOf(10.0);
  public static final Long CUSTOMER_ID = 5678L;
  public static final LocalDateTime CREATED_AT = LocalDateTime.now();

  @Test
  public void shouldConvertEntityToOrder() {
    OrderItemJpaEntity itemEntity = OrderItemJpaEntity.builder().id(1L).productId(2L).quantity(3).price(4).build();

    Order order = OrderJpaEntity.builder().id(ORDER_ID).totalPrice(TOTAL_PRICE).status(OrderStatus.CREATED.toString())
            .customerId(CUSTOMER_ID).createdAt(CREATED_AT).paymentApproved(false).orderItems(singletonList(itemEntity))
            .build().toOrder();

    assertThat(order.getId()).isEqualTo(ORDER_ID);
    assertThat(order.getTotalPrice()).isEqualTo(TOTAL_PRICE);
    assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
    assertThat(order.getCustomerId()).isEqualTo(CUSTOMER_ID);
    assertThat(order.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(order.getPaymentApproved()).isEqualTo(false);
    assertThat(order.getOrderItems()).contains(itemEntity.toOrderItem());
  }

  @Test
  public void shouldConvertEntityFromOrder() {
    OrderItemJpaEntity itemEntity = OrderItemJpaEntity.builder().productId(2L).quantity(3).price(4).build();
    Order order = Order.builder().id(ORDER_ID).totalPrice(TOTAL_PRICE).status(OrderStatus.CREATED).customerId(CUSTOMER_ID)
            .createdAt(CREATED_AT).paymentApproved(true).orderItems(singletonList(itemEntity.toOrderItem())).build();

    OrderJpaEntity orderJpaEntity = OrderJpaEntity.fromOrder(order);

    assertThat(orderJpaEntity.getTotalPrice()).isEqualTo(TOTAL_PRICE);
    assertThat(orderJpaEntity.getStatus()).isEqualTo(OrderStatus.CREATED.toString());
    assertThat(orderJpaEntity.getCustomerId()).isEqualTo(CUSTOMER_ID);
    assertThat(orderJpaEntity.getCreatedAt()).isEqualTo(CREATED_AT);
    assertThat(orderJpaEntity.getPaymentApproved()).isEqualTo(true);
    assertThat(orderJpaEntity.getOrderItems()).contains(itemEntity);
  }

}