package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.domain.MarketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarketItemRepository extends JpaRepository<MarketItem, Long> {
    List<MarketItem> findByCategoryName(String categoryName);
    List<MarketItem> findByPurchasedTrue();
    List<MarketItem> findByIsPremiumTrue();
    @Query("SELECT m FROM MarketItem m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<MarketItem> searchByNameOrDescription(@Param("query") String query);
}