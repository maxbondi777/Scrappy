package com.scrappy.scrappy.repository;

import com.scrappy.scrappy.domain.MarketItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarketItemRepository extends JpaRepository<MarketItemEntity, Long> {

    @Query("SELECT m FROM MarketItemEntity m WHERE m.user.id = :userId AND m.purchased = true")
    List<MarketItemEntity> findByPurchasedTrueAndUserId(@Param("userId") Long userId);

    @Query("SELECT m FROM MarketItemEntity m WHERE m.user.id = :userId AND m.category.name = :categoryName")
    List<MarketItemEntity> findByCategoryNameAndUserId(@Param("categoryName") String categoryName, @Param("userId") Long userId);

    @Query("SELECT m FROM MarketItemEntity m WHERE m.user.id = :userId AND m.isPremium = true")
    List<MarketItemEntity> findByIsPremiumTrueAndUserId(@Param("userId") Long userId);

    @Query("SELECT m FROM MarketItemEntity m WHERE m.name LIKE %:query% OR m.description LIKE %:query%")
    List<MarketItemEntity> searchByNameOrDescription(@Param("query") String query);

}