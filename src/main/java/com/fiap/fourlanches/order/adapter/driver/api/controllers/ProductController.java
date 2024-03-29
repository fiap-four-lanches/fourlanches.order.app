package com.fiap.fourlanches.order.adapter.driver.api.controllers;

import com.fiap.fourlanches.order.adapter.driver.api.controllersAdvisor.ProductControllerAdvisor;
import com.fiap.fourlanches.order.application.dto.ProductDTO;
import com.fiap.fourlanches.order.domain.entities.Category;
import com.fiap.fourlanches.order.domain.entities.Product;
import com.fiap.fourlanches.order.domain.exception.InvalidProductException;
import com.fiap.fourlanches.order.domain.usecases.ProductUseCase;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@ControllerAdvice(assignableTypes = ProductControllerAdvisor.class)
@RequestMapping("products")
public class ProductController {

    private ProductUseCase productUseCase;

    @GetMapping(value = "/{id}", produces = "application/json")
    @ApiResponse(responseCode = "200")
    public Product getProduct(@PathVariable Long id) {
        return productUseCase.getProductById(id);
    }

    @GetMapping(value = "", produces = "application/json")
    @ApiResponse(responseCode = "200")
    public List<Product> getProducts() {
        return productUseCase.getProducts();
    }

    @GetMapping(value = "/categories/{category}", produces = "application/json")
    @ApiResponse(responseCode = "200")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        var categoryParam = Category.fromString(category);
        return productUseCase.getProductsByCategory(categoryParam);
    }

    @PostMapping(value = "", produces = "application/json")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createProduct(@RequestBody ProductDTO productDTO) throws InvalidProductException {
        Long returnedId = productUseCase.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnedId);
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    @ApiResponse(responseCode = "200")
    public void updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) throws InvalidProductException {
        productUseCase.updateProduct(id, productDTO);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    @ApiResponse(responseCode = "200")
    public void deleteProduct(@PathVariable Long id) {
         productUseCase.deleteProduct(id);
    }

}
