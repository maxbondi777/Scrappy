package com.scrappy.scrappy.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "market_items")
@Data
public class MarketItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String subtitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int price;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(nullable = false)
    private double rating;

    @Column(nullable = false)
    private int downloads;

    @Column
    private String iconComponent;

    @Column
    private String iconGradient;

    @Convert(converter = ListToJsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> features;

    @Column
    private int discount;

    @Column
    private boolean isNew;

    @Column
    private boolean isHot;

    @Column
    private boolean isPremium;

    @Column
    private String rarity;

    @Convert(converter = ListToJsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> tags;

    @Column
    private boolean purchased;
}