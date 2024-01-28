package com.fiap.fourlanches.order.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.fourlanches.order.domain.valueobjects.OrderItem;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.valueOf;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private List<OrderItem> orderItems;
    private Long customerId;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private Boolean paymentApproved;

    @JsonIgnore
    public boolean isValid() {
        return !Optional.ofNullable(orderItems).orElse(Collections.emptyList()).isEmpty() && status != null;
    }

    public BigDecimal calculateTotalPrice() {
        return orderItems.stream().reduce(BigDecimal.ZERO, (subtotal, orderItem) ->
                subtotal.add(valueOf(orderItem.getPrice()).multiply(valueOf(orderItem.getQuantity()))), BigDecimal::add);
    }
}
