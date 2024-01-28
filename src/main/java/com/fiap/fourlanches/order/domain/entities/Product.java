package com.fiap.fourlanches.order.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;

import static com.fiap.fourlanches.order.domain.AssertionConcern.isNotEmpty;
import static com.fiap.fourlanches.order.domain.AssertionConcern.isPositive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;
    private Category category;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean isAvailable;

    @JsonIgnore
    public boolean isValid() {
        return ObjectUtils.isNotEmpty(category)
            && isNotEmpty(name)
            && isNotEmpty(description)
            && isPositive(price)
            && isAvailable;
    }
}
