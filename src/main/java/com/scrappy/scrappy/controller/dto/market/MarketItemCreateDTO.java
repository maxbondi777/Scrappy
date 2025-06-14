package com.scrappy.scrappy.controller.dto.market;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class MarketItemCreateDTO {
    @NotBlank(message = "Name is required")
    private String name;

    private String subtitle;

    private String description;

    @Min(value = 0, message = "Price must be non-negative")
    private int price;

    @NotBlank(message = "Category is required")
    private String category;

    @Min(value = 0, message = "Rating must be non-negative")
    @Max(value = 5, message = "Rating must not exceed 5")
    private double rating;

    @Min(value = 0, message = "Downloads must be non-negative")
    private int downloads;

    private String iconComponent;

    private String iconGradient;

    @NotEmpty(message = "Features list cannot be empty")
    private List<String> features;

    @Min(value = 0, message = "Discount must be non-negative")
    @Max(value = 100, message = "Discount must not exceed 100")
    private int discount;

    private boolean isNew;

    private boolean isHot;

    private boolean isPremium;

    @NotBlank(message = "Rarity is required")
    private String rarity;

    @NotEmpty(message = "Tags list cannot be empty")
    private List<String> tags;

    private boolean purchased;
}