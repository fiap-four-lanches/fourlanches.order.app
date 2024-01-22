package com.fiap.fourlanches.order.domain.usecases;

import com.fiap.fourlanches.order.application.dto.ProductDTO;
import com.fiap.fourlanches.order.domain.entities.Category;
import com.fiap.fourlanches.order.domain.entities.Product;

import java.util.List;

public interface ProductUseCase {
    Product getProductById(Long id);
    List<Product> getProducts();
    List<Product> getProductsByCategory(Category category);
    Long createProduct(ProductDTO productDTO);
    void updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
}
