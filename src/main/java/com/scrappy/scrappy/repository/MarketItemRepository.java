package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.entity.MarketItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketItemRepository extends JpaRepository<MarketItem, Long> {
    List<MarketItem> findByCategory(String category);
    List<MarketItem> findByPurchasedTrue();
    List<MarketItem> findByIsPremiumTrue();
    List<MarketItem> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}