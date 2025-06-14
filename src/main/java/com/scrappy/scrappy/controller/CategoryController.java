package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.controller.dto.ApiResponse;
import com.scrappy.scrappy.controller.dto.category.CategoryCreateDTO;
import com.scrappy.scrappy.controller.dto.category.CategoryDTO;
import com.scrappy.scrappy.service.category.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody CategoryCreateDTO createDTO) {
        logger.debug("Received POST /api/market/categories with DTO: {}", createDTO);
        CategoryDTO categoryDTO = categoryService.createCategory(createDTO);
        ApiResponse<CategoryDTO> response = new ApiResponse<>(categoryDTO, null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        logger.debug("Received GET /api/market/categories");
        List<CategoryDTO> categories = categoryService.getAllCategories();
        ApiResponse<List<CategoryDTO>> response = new ApiResponse<>(categories, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}