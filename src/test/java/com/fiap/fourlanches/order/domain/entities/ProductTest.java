package com.fiap.fourlanches.order.domain.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ProductTest {

  @Test
  public void shouldReturnIsValidAsTrueWhenEverythingIsFilledAndIsAvailable() {
    Product product = Product.builder()
            .category(Category.DRINK).name("Guarana").description("Bebida")
            .price(new BigDecimal("10")).isAvailable(true).build();

    assertThat(product.isValid()).isTrue();
  }

  @Test
  public void shouldReturnIsValidAsFalseWhenCategoryIsEmpty() {
    Product product = Product.builder()
            .name("Guarana").description("Bebida")
            .price(new BigDecimal("10")).isAvailable(true).build();

    assertThat(product.isValid()).isFalse();
  }

  @Test
  public void shouldReturnIsValidAsFalseWhenNameIsEmpty() {
    Product product = Product.builder()
            .category(Category.DRINK).description("Bebida")
            .price(new BigDecimal("10")).isAvailable(true).build();

    assertThat(product.isValid()).isFalse();
  }

  @Test
  public void shouldReturnIsValidAsFalseWhenDescriptionIsEmpty() {
    Product product = Product.builder()
            .category(Category.DRINK).name("Guarana")
            .price(new BigDecimal("10")).isAvailable(true).build();

    assertThat(product.isValid()).isFalse();
  }

  @Test
  public void shouldReturnIsValidAsFalseWhenPriceIsNotPositive() {
    Product product = Product.builder()
            .category(Category.DRINK).name("Guarana").description("Bebida")
            .price(new BigDecimal("-10")).isAvailable(true).build();

    assertThat(product.isValid()).isFalse();
  }

  @Test
  public void shouldReturnIsValidAsFalseWhenAvailableIsFalse() {
    Product product = Product.builder()
            .category(Category.DRINK).name("Guarana").description("Bebida")
            .price(new BigDecimal("10")).isAvailable(false).build();

    assertThat(product.isValid()).isFalse();
  }
}