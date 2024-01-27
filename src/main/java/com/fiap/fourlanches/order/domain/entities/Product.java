package com.fiap.fourlanches.order.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;

import static com.fiap.fourlanches.order.domain.AssertationConcern.isNotEmpty;
import static com.fiap.fourlanches.order.domain.AssertationConcern.isPositive;

@Data
@Builder
@AllArgsConstructor
public class Product {

    private Long id;
    private Category category;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean isAvailable;

    public boolean isValid() {
        return ObjectUtils.isNotEmpty(category)
            && isNotEmpty(name)
            && isNotEmpty(description)
            && isPositive(price)
            && isAvailable;
    }
}
