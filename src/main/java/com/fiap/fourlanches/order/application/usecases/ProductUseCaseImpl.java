package com.fiap.fourlanches.order.application.usecases;

import com.fiap.fourlanches.order.domain.entities.Category;
import com.fiap.fourlanches.order.domain.entities.Product;
import com.fiap.fourlanches.order.domain.exception.InvalidProductException;
import com.fiap.fourlanches.order.domain.repositories.ProductRepository;
import com.fiap.fourlanches.order.application.dto.ProductDTO;
import com.fiap.fourlanches.order.domain.usecases.ProductUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ProductUseCaseImpl implements ProductUseCase {

    private final ProductRepository productRepository;

    public Product getProductById(Long id) {
        return productRepository.getProductById(id);
    }

    public List<Product> getProducts() {
        return productRepository.getProducts();
    }

    public List<Product> getProductsByCategory(Category category) {
        return productRepository.getProductsByCategory(category);
    }

    public Long createProduct(ProductDTO productDTO) throws InvalidProductException {
        Product product = productDTO.toProduct();
        if(!product.isValid()) {
            throw new InvalidProductException();
        }
        return productRepository.create(product);
    }

    public void updateProduct(Long id, ProductDTO productDTO) throws InvalidProductException {
        Product product = productDTO.toProduct();
        if(!product.isValid()) {
            throw new InvalidProductException();
        }
        productRepository.getProductById(id);
        product.setId(id);
        productRepository.updateProduct(product);
    }

    public void deleteProduct(Long id) {
         productRepository.deleteProduct(id);
    }
}
