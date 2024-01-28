package com.fiap.fourlanches.order.adapter.driver.api.controllersAdvisor;

import com.fiap.fourlanches.order.adapter.driver.api.exception.InternalServerError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

public class GeneralControllerAdvisor {

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<ApiErrorMessage> handleInternalServerErrorException(WebRequest request) {

        var errorMessage = new ApiErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "an error happened");

        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
