package com.fiap.fourlanches.order.adapter.driven.data.entities;

import com.fiap.fourlanches.order.domain.valueobjects.OrderItem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemJpaEntityTest {

  private static final Long ORDER_ID = 1234L;
  private static final Long PRODUCT_ID = 5678L;
  private static final int QUANTITY = 1;
  private static final double PRICE = 10.0;
  private static final String OBSERVATION = "Observation";

  @Test
  void shouldConvertEntityToOrderItem() {
    OrderItem result = OrderItemJpaEntity.builder()
            .order(OrderJpaEntity.builder().id(ORDER_ID).build())
            .productId(PRODUCT_ID)
            .quantity(QUANTITY)
            .price(PRICE)
            .observation(OBSERVATION)
            .build().toOrderItem();

    assertThat(result).isNotNull();
  }

  @Test
  void shouldConvertOrderItemToEntity() {
    OrderItemJpaEntity result = OrderItemJpaEntity.fromOrderItem(OrderItem.builder()
            .productId(PRODUCT_ID)
            .quantity(QUANTITY)
            .price(PRICE)
            .observation(OBSERVATION)
            .build(), OrderJpaEntity.builder().id(ORDER_ID).build());

    assertThat(result).isNotNull();
  }

}