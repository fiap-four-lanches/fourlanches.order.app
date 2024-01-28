package com.fiap.fourlanches.order.adapter.driver.api.controllersAdvisor;

import com.fiap.fourlanches.order.application.exception.IncorrectOrderStatusException;
import com.fiap.fourlanches.order.domain.exception.InvalidOrderException;
import com.fiap.fourlanches.order.domain.exception.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OrderControllerAdvisor extends GeneralControllerAdvisor {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiErrorMessage> handleOrderNotFoundException() {
        var errorMessage = new ApiErrorMessage(HttpStatus.NOT_FOUND, "Order not found");

        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ApiErrorMessage> handleInvalidOrderException() {
        var errorMessage = new ApiErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY, "Order could not be processed");

        return new ResponseEntity<>(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(IncorrectOrderStatusException.class)
    public ResponseEntity<ApiErrorMessage> handleIncorrectOrderStatusException() {
        var errorMessage = new ApiErrorMessage(HttpStatus.BAD_REQUEST, "Order could not be updated");

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
