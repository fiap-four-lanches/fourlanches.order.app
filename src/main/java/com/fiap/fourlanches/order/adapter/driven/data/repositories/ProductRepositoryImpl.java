package com.fiap.fourlanches.order.adapter.driven.data.repositories;

import com.fiap.fourlanches.order.adapter.driven.data.entities.ProductJpaEntity;
import com.fiap.fourlanches.order.adapter.driven.data.ProductJpaRepository;
import com.fiap.fourlanches.order.domain.entities.Category;
import com.fiap.fourlanches.order.domain.entities.Product;
import com.fiap.fourlanches.order.application.exception.ProductNotFoundException;
import com.fiap.fourlanches.order.domain.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private ProductJpaRepository jpaRepository;

    public Product getProductById(Long id) {
        return jpaRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new).toProduct();
    }

    public List<Product> getProducts() {
        return jpaRepository.findAll().stream().map(ProductJpaEntity::toProduct).collect(Collectors.toList());
    }
    public List<Product> getProductsByCategory(Category category) {
        return jpaRepository.findByCategory(category.toString())
                .stream().map(ProductJpaEntity::toProduct).collect(Collectors.toList());
    }

    public void deleteProduct(Long id) {
        jpaRepository.deleteById(id);
    }

    public void updateProduct(Product product) {
        jpaRepository.save(ProductJpaEntity.fromProduct(product));
    }

    @Override
    public Long createProduct(Product product) {
        return jpaRepository.save(ProductJpaEntity.fromProduct(product)).getId();
    }


}
