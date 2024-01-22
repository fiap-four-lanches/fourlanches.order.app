package com.fiap.fourlanches.order.application;

import java.math.BigDecimal;

public interface PaymentGateway {

    boolean processPayment(long orderId, BigDecimal totalAmount);

}
