package com.fiap.fourlanches.order.adapter.driven.data.entities;

import com.fiap.fourlanches.order.domain.entities.Category;
import com.fiap.fourlanches.order.domain.entities.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductJpaEntityTest {

  private static final Long ID = 1234L;
  private static final BigDecimal PRICE = BigDecimal.valueOf(10.0);
  private static final String NAME = "name";
  private static final String DESCRIPTION = "description";

  @Test
  void shouldConvertEntityToProduct() {
    Product product = ProductJpaEntity.builder()
            .id(ID)
            .name(NAME)
            .description(DESCRIPTION)
            .category(Category.DRINK.name())
            .price(PRICE)
            .isAvailable(true)
            .build().toProduct();

    assertThat(product.getId()).isEqualTo(ID);
    assertThat(product.getName()).isEqualTo(NAME);
    assertThat(product.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(product.getCategory()).isEqualTo(Category.DRINK);
    assertThat(product.getPrice()).isEqualTo(PRICE);
    assertThat(product.isAvailable()).isTrue();
  }

  @Test
  void shouldConvertEntityFromProduct() {
    ProductJpaEntity productJpaEntity = ProductJpaEntity.fromProduct(Product.builder()
            .id(ID)
            .name(NAME)
            .description(DESCRIPTION)
            .category(Category.DRINK)
            .price(PRICE)
            .isAvailable(false)
            .build());

    assertThat(productJpaEntity.getId()).isEqualTo(ID);
    assertThat(productJpaEntity.getName()).isEqualTo(NAME);
    assertThat(productJpaEntity.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(productJpaEntity.getCategory()).isEqualTo(Category.DRINK.toString());
    assertThat(productJpaEntity.getPrice()).isEqualTo(PRICE);
    assertThat(productJpaEntity.getIsAvailable()).isFalse();
  }

}