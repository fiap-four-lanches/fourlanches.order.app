package com.fiap.fourlanches.order.domain.entities;

import com.fiap.fourlanches.order.domain.valueobjects.OrderItem;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OrderTest {

  @Test
  public void shouldReturnHundredAsTotalPriceWhenPriceIsTenAndQuantityIsTen() {
    Order order = Order.builder()
            .orderItems(singletonList(OrderItem.builder()
                    .price(10)
                    .quantity(10)
                    .build()))
            .build();

    assertThat(order.calculateTotalPrice()).isEqualTo(new BigDecimal("100.0"));
  }

  @Test
  public void shouldReturnIsValidAsFalseWhenOrderItemsIsEmptyAndStatusIsNotNull() {
    Order order = Order.builder().orderItems(emptyList())
            .status(OrderStatus.CREATED)
            .build();

    assertThat(order.isValid()).isFalse();
  }

  @Test
  public void shouldReturnIsValidAsFalseWhenOrderItemsIsNotEmptyAndStatusIsNull() {
    Order order = Order.builder().orderItems(singletonList(OrderItem.builder()
            .build())).build();

    assertThat(order.isValid()).isFalse();
  }

  @Test
  public void shouldReturnIsValidAsTrueWhenOrderItemsIsNotEmpty() {
    Order order = Order.builder()
            .status(OrderStatus.CREATED)
            .orderItems(singletonList(OrderItem.builder().build())).build();

    assertThat(order.isValid()).isTrue();
  }

}