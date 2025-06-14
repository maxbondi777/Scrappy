package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.domain.MarketItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarketItemRepository extends JpaRepository<MarketItemEntity, Long> {
    List<MarketItemEntity> findByCategoryName(String categoryName);
    List<MarketItemEntity> findByPurchasedTrue();
    List<MarketItemEntity> findByIsPremiumTrue();
    @Query("SELECT m FROM MarketItem m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<MarketItemEntity> searchByNameOrDescription(@Param("query") String query);
}