package com.fiap.fourlanches.order.application.dto;

import com.fiap.fourlanches.order.domain.entities.Category;
import com.fiap.fourlanches.order.domain.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Category category;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean isAvailable;

    public Product toProduct() {
        return Product.builder()
                .category(category)
                .name(name)
                .description(description)
                .price(price)
                .isAvailable(isAvailable)
                .build();
    }
}
