package com.scrappy.scrappy.controller.dto;

import com.scrappy.scrappy.domain.CategoryType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NoteDTO {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private CategoryType category;
    private List<String> tags;
    private boolean isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}