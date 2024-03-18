package com.fiap.fourlanches.order.adapter.driver.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fiap.fourlanches.order.adapter.driver.api.controllersAdvisor.ApiErrorMessage;
import com.fiap.fourlanches.order.adapter.driver.api.controllersAdvisor.ProductControllerAdvisor;
import com.fiap.fourlanches.order.application.dto.ProductDTO;
import com.fiap.fourlanches.order.domain.entities.Product;
import com.fiap.fourlanches.order.domain.usecases.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static com.fiap.fourlanches.order.domain.entities.Category.DRINK;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfiguration.class)
class ProductControllerTest {

  private static final long PRODUCT_ID = 1234L;

  @Autowired
  private MockMvc mvc;

  @Mock
  private ProductUseCase productUseCase;

  @InjectMocks
  private ProductController productController;

  @Autowired
  private JacksonTester<Product> jsonProduct;

  @Autowired
  private JacksonTester<List<Product>> jsonProducts;

  @Autowired
  private JacksonTester<ApiErrorMessage> jsonApiErrorMessage;

  @BeforeEach
  void setup() {
    this.mvc = MockMvcBuilders
            .standaloneSetup(productController)
            .setControllerAdvice(new ProductControllerAdvisor())
            .build();
  }

  @Test
  void givenId_whenProductIsFound_ThenReturnProduct() throws Exception {
    when(productUseCase.getProductById(PRODUCT_ID)).thenReturn(getDefaultProduct());

    MockHttpServletResponse response = mvc.perform(get("/products/" + PRODUCT_ID)
            .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isEqualTo(jsonProduct.write(getDefaultProduct()).getJson());
  }

  @Test
  void whenProductsAreFound_ThenReturnProducts() throws Exception {
    var expected = singletonList(getDefaultProduct());
    when(productUseCase.getProducts()).thenReturn(expected);

    MockHttpServletResponse response = mvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();

    List<Product> products = new ObjectMapper().readValue(response.getContentAsString() , getProductListType());
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isEqualTo(jsonProducts.write(products).getJson());
  }

  @Test
  void givenCategory_whenProductsWithSameCategoryAreFound_ThenReturnProducts() throws Exception {
    var expected = singletonList(getDefaultProduct());
    when(productUseCase.getProductsByCategory(eq(DRINK))).thenReturn(expected);

    MockHttpServletResponse response = mvc.perform(get("/products/categories/" + DRINK)
            .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    List<Product> products = new ObjectMapper().readValue(response.getContentAsString() , getProductListType());
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isEqualTo(jsonProducts.write(products).getJson());
  }

  @Test
  void givenProductToBeSaved_whenSaveIsSuccessful_ThenReturnProduct() throws Exception {
    ProductDTO productDTO = getProductDTO();
    when(productUseCase.createProduct(productDTO)).thenReturn(PRODUCT_ID);

    var response = mvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
            .content(getMapper().writeValueAsString(productDTO))).andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.getContentAsString()).isEqualTo(getMapper().writeValueAsString(PRODUCT_ID));
  }

  @Test
  void givenId_whenUpdateProduct_ThenVerifyUpdateIsCalled() throws Exception {
    ProductDTO productDTO = getProductDTO();

    var response = mvc.perform(put("/products/" + PRODUCT_ID).contentType(MediaType.APPLICATION_JSON)
            .content(getMapper().writeValueAsString(productDTO))).andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    verify(productUseCase).updateProduct(eq(PRODUCT_ID), eq(productDTO));
  }

  @Test
  void givenId_whenDeleteProduct_ThenVerifyDeleteIsCalled() throws Exception {
    MockHttpServletResponse response = mvc.perform(delete("/products/" + PRODUCT_ID)
            .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    verify(productUseCase).deleteProduct(PRODUCT_ID);
  }

  private ProductDTO getProductDTO() {
    return ProductDTO.builder()
            .category(DRINK)
            .name("Coca Cola")
            .description("")
            .price(BigDecimal.valueOf(10.0))
            .isAvailable(true)
            .build();
  }

  private Product getDefaultProduct() {
    return Product.builder().id(PRODUCT_ID).build();
  }

  private ObjectWriter getMapper() {
    return new ObjectMapper().writer().withDefaultPrettyPrinter();
  }

  private CollectionType getProductListType() {
    return TypeFactory.defaultInstance().constructCollectionType(List.class, Product.class);
  }
}