package com.scrappy.scrappy.service;

import com.scrappy.scrappy.controller.dto.CategoryCreateDTO;
import com.scrappy.scrappy.controller.dto.CategoryDTO;
import com.scrappy.scrappy.domain.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO toDto(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public Category toEntity(CategoryCreateDTO createDTO) {
        Category category = new Category();
        category.setName(createDTO.getName());
        return category;
    }
}