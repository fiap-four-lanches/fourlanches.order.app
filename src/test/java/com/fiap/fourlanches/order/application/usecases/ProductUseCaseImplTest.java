package com.fiap.fourlanches.order.application.usecases;

import com.fiap.fourlanches.order.application.dto.ProductDTO;
import com.fiap.fourlanches.order.domain.entities.Product;
import com.fiap.fourlanches.order.domain.exception.InvalidProductException;
import com.fiap.fourlanches.order.domain.repositories.ProductRepository;
import com.fiap.fourlanches.order.domain.usecases.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.fiap.fourlanches.order.domain.entities.Category.DRINK;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseImplTest {

  private static final long PRODUCT_ID = 1234L;
  @Mock
  private ProductRepository productRepository;

  private ProductUseCase productUseCase;

  @BeforeEach
  void setUp() {
      productUseCase = new ProductUseCaseImpl(productRepository);
  }

  @Test
  void givenId_whenGetProductById_thenReturnProduct() {
    Product product = getProductDTO().toProduct();
    when(productRepository.getProductById(PRODUCT_ID)).thenReturn(product);

    Product actualProduct = productUseCase.getProductById(PRODUCT_ID);

    assertThat(actualProduct).isEqualTo(product);
  }

  @Test
  void whenGetProducts_thenReturnProduct() {
    Product product = getProductDTO().toProduct();
    when(productRepository.getProducts()).thenReturn(singletonList(product));

    List<Product> products = productUseCase.getProducts();

    assertThat(products).contains(product);
  }

  @Test
  void givenCategory_whenProductsWithSameCategoryAreFound_ThenReturnProducts() {
    Product product = getProductDTO().toProduct();
    when(productRepository.getProductsByCategory(eq(DRINK))).thenReturn(singletonList(product));

    List<Product> products = productUseCase.getProductsByCategory(DRINK);

    assertThat(products).contains(product);
  }

  @Test
  void givenProductToBeCreated_whenCreateIsSuccessful_ThenReturnId() {
    when(productRepository.createProduct(getProductDTO().toProduct())).thenReturn(PRODUCT_ID);

    Long actualId = productUseCase.createProduct(getProductDTO());

    assertThat(actualId).isEqualTo(PRODUCT_ID);
  }

  @Test
  void givenProductToBeCreated_whenCreateFails_ThenError() {
    assertThrows(InvalidProductException.class,
            () -> productUseCase.createProduct(getInvalidProductDTO()));
  }

  @Test
  void givenProductToBeUpdated_whenUpdateIsSuccessful_ThenReturnId() {
    productUseCase.updateProduct(PRODUCT_ID, getProductDTO());

    Product expectedProduct = getProductDTO().toProduct();
    expectedProduct.setId(PRODUCT_ID);
    verify(productRepository).updateProduct(expectedProduct);
  }

  @Test
  void givenProductToBeUpdated_whenUpdateFails_ThenError() {
    assertThrows(InvalidProductException.class,
            () -> productUseCase.updateProduct(PRODUCT_ID, getInvalidProductDTO()));
  }

  @Test
  void givenId_whenDeleteProduct_ThenVerifyDeleteIsCalled() {
    productUseCase.deleteProduct(PRODUCT_ID);

    verify(productRepository).deleteProduct(PRODUCT_ID);
  }

  private ProductDTO getInvalidProductDTO() {
    return ProductDTO.builder()
            .category(DRINK)
            .name("")
            .description("")
            .price(BigDecimal.valueOf(10.0))
            .isAvailable(true)
            .build();
  }

  private ProductDTO getProductDTO() {
    return ProductDTO.builder()
            .category(DRINK)
            .name("Coca Cola")
            .description("Coca Cola 600ml")
            .price(BigDecimal.valueOf(10.0))
            .isAvailable(true)
            .build();
  }

}