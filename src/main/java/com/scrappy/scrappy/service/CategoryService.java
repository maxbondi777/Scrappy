package com.scrappy.scrappy.service;

import com.scrappy.scrappy.controller.dto.CategoryCreateDTO;
import com.scrappy.scrappy.controller.dto.CategoryDTO;
import com.scrappy.scrappy.domain.Category;
import com.scrappy.scrappy.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryDTO createCategory(CategoryCreateDTO createDTO) {
        logger.debug("Creating category with DTO: {}", createDTO);
        if (categoryRepository.findByName(createDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Category already exists: " + createDTO.getName());
        }
        Category category = categoryMapper.toEntity(createDTO);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        logger.debug("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
}