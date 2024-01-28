package com.fiap.fourlanches.order.adapter.driven.data.entities;

import com.fiap.fourlanches.order.domain.valueobjects.OrderItem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderItemJpaEntityTest {

  public static final Long ORDER_ID = 1234L;
  public static final Long PRODUCT_ID = 5678L;
  public static final int QUANTITY = 1;
  public static final double PRICE = 10.0;
  public static final String OBSERVATION = "Observation";

  @Test
  public void shouldConvertEntityToOrderItem() {
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
  public void shouldConvertOrderItemToEntity() {
    OrderItemJpaEntity result = OrderItemJpaEntity.fromOrderItem(OrderItem.builder()
            .productId(PRODUCT_ID)
            .quantity(QUANTITY)
            .price(PRICE)
            .observation(OBSERVATION)
            .build(), OrderJpaEntity.builder().id(ORDER_ID).build());

    assertThat(result).isNotNull();
  }

}