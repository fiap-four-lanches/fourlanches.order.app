package com.fiap.fourlanches.order.adapter.driver.api.controllersAdvisor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ProductControllerAdvisorTest {

  private ProductControllerAdvisor productControllerAdvisor;

  @BeforeEach
  void setUp() {
    productControllerAdvisor = new ProductControllerAdvisor();
  }

  @Test
  void shouldHandleInternalServerErrorException() {
    var expectedErrorMessage = new ApiErrorMessage(HttpStatus.NOT_FOUND, "Product not found");

    ResponseEntity<ApiErrorMessage> response = productControllerAdvisor.handleProductNotFoundException();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isEqualTo(expectedErrorMessage);
  }

  @Test
  void shouldHandleInvalidOrderException() {
    var expectedErrorMessage = new ApiErrorMessage(HttpStatus.BAD_REQUEST, "Invalid product");

    ResponseEntity<ApiErrorMessage> response = productControllerAdvisor.handleInvalidProductException();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo(expectedErrorMessage);
  }
}