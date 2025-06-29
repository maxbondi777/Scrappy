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

    private String name;
    private String subtitle;
    private String description;
    private double price;
    private String category;
    private double rating;
    private int downloads;
    private String iconGradient;
    private List<String> features;
    private double discount;
    private boolean isNew;
    private boolean isHot;
    private boolean isPremium;
    private String rarity;
    private List<String> tags;
    private boolean purchased;
}