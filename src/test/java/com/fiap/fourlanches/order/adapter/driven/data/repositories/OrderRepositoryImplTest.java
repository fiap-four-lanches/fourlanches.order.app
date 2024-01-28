package com.fiap.fourlanches.order.adapter.driven.data.repositories;

import com.fiap.fourlanches.order.adapter.driven.data.OrderJpaRepository;
import com.fiap.fourlanches.order.adapter.driven.data.entities.OrderJpaEntity;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.repositories.OrderRepository;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.CREATED;
import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.IN_PREPARATION;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

  private static final long ORDER_ID = 1234L;

  @Mock
  private OrderJpaRepository jpaRepository;

  private OrderRepository orderRepository;

  @BeforeEach
  void setup() {
    this.orderRepository = new OrderRepositoryImpl(jpaRepository);
  }

  @Test
  void shouldCreateOrder() {
    when(jpaRepository.save(any(OrderJpaEntity.class))).thenReturn(getDefaultOrderEntity());

    Order expectedOrder = orderRepository.createOrder(getDefaultOrderEntity().toOrder());

    verify(jpaRepository).save(eq(getDefaultOrderEntity()));
    assertThat(expectedOrder).isNotNull();
  }

  @Test
  void shouldGetOrderById() {
    when(jpaRepository.getReferenceById(eq(ORDER_ID))).thenReturn(getDefaultOrderEntity());
    Order expectedOrder = orderRepository.getById(ORDER_ID);

    assertThat(expectedOrder).isNotNull();
  }

  @Test
  void shouldSaveOrder() {
    Boolean expected = orderRepository.save(getDefaultOrderEntity().toOrder());

    verify(jpaRepository).save(eq(getDefaultOrderEntity()));
    assertThat(expected).isTrue();
  }

  @Test
  void shouldGetOrdersByStatus() {
    when(jpaRepository.findByStatus(eq(CREATED.toString())))
            .thenReturn(singletonList(getDefaultOrderEntity()));

    List<Order> expectedList = orderRepository.getOrdersByStatus(CREATED);

    assertThat(expectedList).contains(getDefaultOrderEntity().toOrder());
  }

  @Test
  void shouldGetAllOrdersOrderedByStatusAndCreatedAt() {
    when(jpaRepository.getAllPendingOrdersOrderedByStatusAndCreatedAt())
            .thenReturn(singletonList(getDefaultOrderEntity()));

    List<Order> expectedList = orderRepository.getAllOrdersOrderedByStatusAndCreatedAt();

    assertThat(expectedList).contains(getDefaultOrderEntity().toOrder());
  }

  @Test
  void shouldUpdateOrder() {
    when(jpaRepository.getReferenceById(eq(ORDER_ID))).thenReturn(OrderJpaEntity.builder().id(ORDER_ID)
            .orderItems(emptyList()).build());

    OrderJpaEntity updatedOrder = OrderJpaEntity.builder()
            .id(ORDER_ID)
            .status(IN_PREPARATION.toString())
            .paymentApproved(true)
            .orderItems(emptyList())
            .build();

    orderRepository.updateOrder(ORDER_ID, Order.builder().status(IN_PREPARATION).paymentApproved(true).build());

    verify(jpaRepository).save(eq(updatedOrder));
  }

  private static OrderJpaEntity getDefaultOrderEntity() {
    return OrderJpaEntity.fromOrder(Order.builder()
            .id(ORDER_ID)
            .status(CREATED)
            .paymentApproved(false)
                    .orderItems(emptyList())
            .build());
  }
}