package com.fiap.fourlanches.order.adapter.driver.api.controllers;

import com.fiap.fourlanches.order.adapter.driver.api.controllersAdvisor.OrderControllerAdvisor;
import com.fiap.fourlanches.order.application.dto.OrderDTO;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.exception.InvalidOrderException;
import com.fiap.fourlanches.order.domain.exception.OrderNotFoundException;
import com.fiap.fourlanches.order.domain.usecases.OrderUseCase;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fiap.fourlanches.order.application.constants.HeaderConstant.X_REQUEST_ID;

@RestController
@AllArgsConstructor
@ControllerAdvice(assignableTypes = OrderControllerAdvisor.class)
@RequestMapping("orders")
public class OrderController {

    private OrderUseCase orderUseCase;

    @GetMapping(value = "", produces = "application/json")
    @ApiResponse(responseCode = "200")
    public List<Order> getOrders() {
        return orderUseCase.getAllPendingOrdersOrderedByStatusAndCreatedAt();
    }

    @PostMapping(value = "", produces = "application/json")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createOrder(@RequestHeader(X_REQUEST_ID) String xRequestId,
                                            @RequestBody OrderDTO orderDTO) throws InvalidOrderException {
        Map<String, Object> headers = new HashMap<>();
        headers.put(X_REQUEST_ID, xRequestId);
        Long orderId = orderUseCase.createOrder(orderDTO, headers);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

    @PatchMapping(value = "/{orderId}/in_preparation", produces = "application/json")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> orderInPreparation(@PathVariable Long orderId)
            throws InvalidOrderException, OrderNotFoundException {
        orderUseCase.orderInPreparation(orderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{orderId}/ready", produces = "application/json")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> orderReady(@PathVariable Long orderId)
            throws InvalidOrderException {
        orderUseCase.orderReady(orderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{orderId}/finished", produces = "application/json")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> orderFinished(@PathVariable Long orderId)
            throws InvalidOrderException {
        orderUseCase.orderFinished(orderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{orderId}/cancel", produces = "application/json")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> orderCanceled(@PathVariable Long orderId) {
        orderUseCase.orderCanceled(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/status/{status}", produces = "application/json")
    @ApiResponse(responseCode = "200")
    public List<Order> getOrdersByStatus(@PathVariable OrderStatus status) {
        return orderUseCase.getOrdersByStatus(status);
    }

}
