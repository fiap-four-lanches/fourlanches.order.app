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

  @Test
  void shouldHandleFailPublishToQueueException() {
    var expectedErrorMessage = new ApiErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "Fail to publish message to queue");

    ResponseEntity<ApiErrorMessage> response = orderControllerAdvisor.handleFailPublishToQueueException();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isEqualTo(expectedErrorMessage);
  }

  @Test
  void shouldHandleJsonProcessingException() {
    var expectedErrorMessage = new ApiErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating message to send to queue");

    ResponseEntity<ApiErrorMessage> response = orderControllerAdvisor.handleJsonProcessingException();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isEqualTo(expectedErrorMessage);
  }

  @Test
  void shouldHandleAmqpExceptionn() {
    var expectedErrorMessage = new ApiErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error happened while sending message to queue");

    ResponseEntity<ApiErrorMessage> response = orderControllerAdvisor.handleAmqpException();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isEqualTo(expectedErrorMessage);
  }

}