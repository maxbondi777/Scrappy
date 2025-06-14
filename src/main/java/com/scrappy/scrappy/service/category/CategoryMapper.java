package com.scrappy.scrappy.service.category;

import com.scrappy.scrappy.controller.dto.category.CategoryCreateDTO;
import com.scrappy.scrappy.controller.dto.category.CategoryDTO;
import com.scrappy.scrappy.domain.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO toDto(CategoryEntity category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public CategoryEntity toEntity(CategoryCreateDTO createDTO) {
        CategoryEntity category = new CategoryEntity();
        category.setName(createDTO.getName());
        return category;
    }
}