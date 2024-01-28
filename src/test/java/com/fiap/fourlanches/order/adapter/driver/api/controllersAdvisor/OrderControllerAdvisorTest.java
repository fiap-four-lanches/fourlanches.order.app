package com.fiap.fourlanches.order.adapter.driver.api.controllersAdvisor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class OrderControllerAdvisorTest {

  private OrderControllerAdvisor orderControllerAdvisor;

  @BeforeEach
  void setUp() {
    orderControllerAdvisor = new OrderControllerAdvisor();
  }

  @Test
  void shouldHandleInternalServerErrorException() {
    var expectedErrorMessage = new ApiErrorMessage(HttpStatus.NOT_FOUND, "Order not found");

    ResponseEntity<ApiErrorMessage> response = orderControllerAdvisor.handleOrderNotFoundException();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isEqualTo(expectedErrorMessage);
  }

  @Test
  void shouldHandleInvalidOrderException() {
    var expectedErrorMessage = new ApiErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY, "Order could not be processed");

    ResponseEntity<ApiErrorMessage> response = orderControllerAdvisor.handleInvalidOrderException();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(response.getBody()).isEqualTo(expectedErrorMessage);
  }

  @Test
  void shouldHandleIncorrectOrderStatusException() {
    var expectedErrorMessage = new ApiErrorMessage(HttpStatus.BAD_REQUEST, "Order could not be updated");

    ResponseEntity<ApiErrorMessage> response = orderControllerAdvisor.handleIncorrectOrderStatusException();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo(expectedErrorMessage);
  }

}