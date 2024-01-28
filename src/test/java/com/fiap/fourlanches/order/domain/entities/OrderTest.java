package com.fiap.fourlanches.order.domain.entities;

import com.fiap.fourlanches.order.domain.valueobjects.OrderItem;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OrderTest {

  @Test
  void shouldReturnHundredAsTotalPriceWhenPriceIsTenAndQuantityIsTen() {
    Order order = Order.builder()
            .orderItems(List.of(
                    OrderItem.builder().price(10).quantity(10).build(),
                    OrderItem.builder().price(5).quantity(20).build()))
            .build();

    assertThat(order.calculateTotalPrice()).isEqualTo(new BigDecimal("200.0"));
  }

  @Test
  void shouldReturnIsValidAsFalseWhenOrderItemsIsEmptyAndStatusIsNotNull() {
    Order order = Order.builder().orderItems(emptyList())
            .status(OrderStatus.CREATED)
            .build();

    assertThat(order.isValid()).isFalse();
  }

  @Test
  void shouldReturnIsValidAsFalseWhenOrderItemsIsNotEmptyAndStatusIsNull() {
    Order order = Order.builder().orderItems(singletonList(OrderItem.builder()
            .build())).build();

    assertThat(order.isValid()).isFalse();
  }

  @Test
  void shouldReturnIsValidAsTrueWhenOrderItemsIsNotEmpty() {
    Order order = Order.builder()
            .status(OrderStatus.CREATED)
            .orderItems(singletonList(OrderItem.builder().build())).build();

    assertThat(order.isValid()).isTrue();
  }

}