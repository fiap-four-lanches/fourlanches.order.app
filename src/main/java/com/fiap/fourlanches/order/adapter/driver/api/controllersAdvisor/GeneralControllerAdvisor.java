package com.fiap.fourlanches.order.adapter.driver.api.controllersAdvisor;

import com.fiap.fourlanches.order.adapter.driver.api.exception.InternalServerError;
import com.fiap.fourlanches.order.domain.exception.OrderNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GeneralControllerAdvisor {

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<ApiErrorMessage> handleInternalServerErrorException(WebRequest request) {

        var errorMessage = new ApiErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "an error happened");

        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiErrorMessage> handleOrderNotFoundException(OrderNotFoundException ex, WebRequest request) {

        var errorMessage = new ApiErrorMessage(HttpStatus.NOT_FOUND, "resource not found");

        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }
}
