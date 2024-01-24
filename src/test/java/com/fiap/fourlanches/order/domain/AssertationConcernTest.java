package com.fiap.fourlanches.order.domain;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.fiap.fourlanches.order.domain.AssertationConcern.isNotEmpty;
import static com.fiap.fourlanches.order.domain.AssertationConcern.isPositive;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AssertationConcernTest {

  @Test
  public void shouldReturnIsNotEmptyAsTrueWhenStringIsFourlanches() {
    boolean expected = isNotEmpty("Fourlanches");
    assertThat(expected).isTrue();
  }

  @Test
  public void shouldReturnIsNotEmptyAsFalseWhenStringIsNull() {
    boolean expected = isNotEmpty(null);
    assertThat(expected).isFalse();
  }

  @Test
  public void shouldReturnIsNotEmptyAsFalseWhenStringIsEmpty() {
    boolean expected = isNotEmpty("");
    assertThat(expected).isFalse();
  }

  @Test
  public void shouldReturnIsPositiveAsTrueWhenValueIsTen() {
    boolean expected = isPositive(new BigDecimal("10"));
    assertThat(expected).isTrue();
  }

  @Test
  public void shouldReturnIsPositiveAsFalseWhenValueIsNull() {
    boolean expected = isPositive(null);
    assertThat(expected).isFalse();
  }

  @Test
  public void shouldReturnIsPositiveAsFalseWhenValueIsNegtive() {
    boolean expected = isPositive(new BigDecimal("-10"));
    assertThat(expected).isFalse();
  }

  @Test
  public void shouldReturnIsPositiveAsFalseWhenValueIsZero() {
    boolean expected = isPositive(new BigDecimal("0"));
    assertThat(expected).isFalse();
  }

}