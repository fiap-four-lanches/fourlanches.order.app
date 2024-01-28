package com.fiap.fourlanches.order.domain.repositories;

import com.fiap.fourlanches.order.domain.entities.Category;
import com.fiap.fourlanches.order.domain.entities.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository {

    Product getProductById(Long id);

    List<Product> getProducts();

    List<Product> getProductsByCategory(Category category);

    Long createProduct(Product product);

    void deleteProduct(Long id);

    void updateProduct(Product product);
}
