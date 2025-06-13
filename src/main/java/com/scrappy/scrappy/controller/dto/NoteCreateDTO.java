package com.scrappy.scrappy.controller.dto;

import com.scrappy.scrappy.domain.CategoryType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class NoteCreateDTO {
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;

    @NotNull(message = "Category is required")
    private CategoryType category;

    @Size(max = 8, message = "Maximum 8 tags allowed")
    private List<@Size(max = 20, message = "Each tag must not exceed 20 characters") String> tags;
}