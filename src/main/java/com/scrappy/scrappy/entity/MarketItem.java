package com.scrappy.scrappy.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "market_items")
public class MarketItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String subtitle;
    private String description;
    private Integer price;
    private String category;
    private Double rating;
    private Integer downloads;
    private String iconComponent;
    private String iconGradient;
    @ElementCollection
    private List<String> features;
    private Integer discount;
    private Boolean isNew;
    private Boolean isHot;
    private Boolean isPremium;
    private String rarity;
    @ElementCollection
    private List<String> tags;
    private Boolean purchased;
}