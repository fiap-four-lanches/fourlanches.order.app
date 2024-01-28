package com.fiap.fourlanches.order.adapter.driven.data.repositories;

import com.fiap.fourlanches.order.adapter.driven.data.ProductJpaRepository;
import com.fiap.fourlanches.order.adapter.driven.data.entities.ProductJpaEntity;
import com.fiap.fourlanches.order.application.exception.ProductNotFoundException;
import com.fiap.fourlanches.order.domain.entities.Category;
import com.fiap.fourlanches.order.domain.entities.Product;
import com.fiap.fourlanches.order.domain.repositories.ProductRepository;
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductRepositoryImplTest {

  public static final long PRODUCT_ID = 1234L;
  @Mock
  private ProductJpaRepository jpaRepository;

  private ProductRepository productRepository;

  @BeforeEach
  public void setup() {
    this.productRepository = new ProductRepositoryImpl(jpaRepository);
  }

  @Test
  public void shouldGetProductById() {
    when(jpaRepository.findById(eq(PRODUCT_ID))).thenReturn(Optional.of(getDefaultProductEntity()));

    Product expectedProduct = productRepository.getProductById(PRODUCT_ID);

    assertThat(expectedProduct).isNotNull();
  }

  @Test
  public void shouldThrowProductNotFoundWhenGetProductByIdDoesNotExist() {
    assertThrows(ProductNotFoundException.class, () -> productRepository.getProductById(PRODUCT_ID));
  }

  @Test
  public void shouldGetProducts() {
    when(jpaRepository.findAll()).thenReturn(singletonList(getDefaultProductEntity()));

    List<Product> expectedList = productRepository.getProducts();

    assertThat(expectedList).contains(getDefaultProductEntity().toProduct());
  }

  @Test
  public void shouldGetProductByCategory() {
    when(jpaRepository.findByCategory(eq(Category.DRINK.toString())))
            .thenReturn(singletonList(getDefaultProductEntity()));

    List<Product> expectedList = productRepository.getProductsByCategory(Category.DRINK);

    assertThat(expectedList).contains(getDefaultProductEntity().toProduct());
  }

  @Test
  public void shouldDeleteProduct() {
    productRepository.deleteProduct(PRODUCT_ID);

    verify(jpaRepository).deleteById(eq(PRODUCT_ID));
  }

  @Test
  public void shouldUpdateProduct() {
    productRepository.updateProduct(getDefaultProductEntity().toProduct());

    verify(jpaRepository).save(eq(getDefaultProductEntity()));
  }

  @Test
  public void shouldCreateProduct() {
    when(jpaRepository.save(any(ProductJpaEntity.class))).thenReturn(getDefaultProductEntity());

    Long expectedProductId = productRepository.createProduct(getDefaultProductEntity().toProduct());

    verify(jpaRepository).save(eq(getDefaultProductEntity()));
    assertThat(expectedProductId).isEqualTo(PRODUCT_ID);
  }

  private static ProductJpaEntity getDefaultProductEntity() {
    return ProductJpaEntity.fromProduct(Product.builder()
            .id(PRODUCT_ID)
            .category(Category.DRINK)
            .build());
  }

}