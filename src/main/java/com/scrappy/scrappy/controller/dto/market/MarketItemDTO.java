package com.scrappy.scrappy.controller.dto.market;

import lombok.Data;

import java.util.List;

@Data
public class MarketItemDTO {
    private Long id;
    private String name;
    private String subtitle;
    private String description;
    private int price;
    private String category;
    private double rating;
    private int downloads;
    private String iconComponent;
    private String iconGradient;
    private List<String> features;
    private int discount;
    private boolean isNew;
    private boolean isHot;
    private boolean isPremium;
    private String rarity;
    private List<String> tags;
    private boolean purchased;
}