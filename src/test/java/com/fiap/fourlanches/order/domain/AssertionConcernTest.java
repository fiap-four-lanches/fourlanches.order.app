package com.fiap.fourlanches.order.domain;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.fiap.fourlanches.order.domain.AssertionConcern.isNotEmpty;
import static com.fiap.fourlanches.order.domain.AssertionConcern.isPositive;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AssertionConcernTest {

  @Test
  void shouldReturnIsNotEmptyAsTrueWhenStringIsFourlanches() {
    boolean expected = isNotEmpty("Fourlanches");
    assertThat(expected).isTrue();
  }

  @Test
  void shouldReturnIsNotEmptyAsFalseWhenStringIsNull() {
    boolean expected = isNotEmpty(null);
    assertThat(expected).isFalse();
  }

  @Test
  void shouldReturnIsNotEmptyAsFalseWhenStringIsEmpty() {
    boolean expected = isNotEmpty("");
    assertThat(expected).isFalse();
  }

  @Test
  void shouldReturnIsPositiveAsTrueWhenValueIsTen() {
    boolean expected = isPositive(new BigDecimal("10"));
    assertThat(expected).isTrue();
  }

  @Test
  void shouldReturnIsPositiveAsFalseWhenValueIsNull() {
    boolean expected = isPositive(null);
    assertThat(expected).isFalse();
  }

  @Test
  void shouldReturnIsPositiveAsFalseWhenValueIsNegtive() {
    boolean expected = isPositive(new BigDecimal("-10"));
    assertThat(expected).isFalse();
  }

  @Test
  void shouldReturnIsPositiveAsFalseWhenValueIsZero() {
    boolean expected = isPositive(new BigDecimal("0"));
    assertThat(expected).isFalse();
  }

}