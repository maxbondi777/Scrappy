package com.scrappy.scrappy.controller.dto.category;

import lombok.Data;

import com.scrappy.scrappy.domain.CategoryType;
import lombok.Data;

@Data
public class CategoryStatsDTO {
    private String category;
    private long count;

    // Конструктор для JPQL
    public CategoryStatsDTO(CategoryType category, Long count) {
        this.category = category.toString();
        this.count = count;
    }
}