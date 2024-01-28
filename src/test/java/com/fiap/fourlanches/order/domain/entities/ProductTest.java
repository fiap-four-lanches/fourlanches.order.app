package com.fiap.fourlanches.order.domain.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ProductTest {

  @Test
  void shouldReturnIsValidAsTrueWhenEverythingIsFilledAndIsAvailable() {
    Product product = Product.builder()
            .category(Category.DRINK).name("Guarana").description("Bebida")
            .price(new BigDecimal("10")).isAvailable(true).build();

    assertThat(product.isValid()).isTrue();
  }

  @Test
  void shouldReturnIsValidAsFalseWhenCategoryIsEmpty() {
    Product product = Product.builder()
            .name("Guarana").description("Bebida")
            .price(new BigDecimal("10")).isAvailable(true).build();

    assertThat(product.isValid()).isFalse();
  }

  @Test
  void shouldReturnIsValidAsFalseWhenNameIsEmpty() {
    Product product = Product.builder()
            .category(Category.DRINK).description("Bebida")
            .price(new BigDecimal("10")).isAvailable(true).build();

    assertThat(product.isValid()).isFalse();
  }

  @Test
  void shouldReturnIsValidAsFalseWhenDescriptionIsEmpty() {
    Product product = Product.builder()
            .category(Category.DRINK).name("Guarana")
            .price(new BigDecimal("10")).isAvailable(true).build();

    assertThat(product.isValid()).isFalse();
  }

  @Test
  void shouldReturnIsValidAsFalseWhenPriceIsNotPositive() {
    Product product = Product.builder()
            .category(Category.DRINK).name("Guarana").description("Bebida")
            .price(new BigDecimal("-10")).isAvailable(true).build();

    assertThat(product.isValid()).isFalse();
  }

  @Test
  void shouldReturnIsValidAsFalseWhenAvailableIsFalse() {
    Product product = Product.builder()
            .category(Category.DRINK).name("Guarana").description("Bebida")
            .price(new BigDecimal("10")).isAvailable(false).build();

    assertThat(product.isValid()).isFalse();
  }
}