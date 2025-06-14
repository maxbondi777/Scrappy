package com.scrappy.scrappy.service.market;

import com.scrappy.scrappy.controller.dto.market.MarketItemCreateDTO;
import com.scrappy.scrappy.controller.dto.market.MarketItemDTO;
import com.scrappy.scrappy.domain.CategoryEntity;
import com.scrappy.scrappy.domain.MarketItemEntity;
import org.springframework.stereotype.Component;

@Component
public class MarketItemMapper {

    public MarketItemDTO toDto(MarketItemEntity marketItem) {
        MarketItemDTO dto = new MarketItemDTO();
        dto.setId(marketItem.getId());
        dto.setName(marketItem.getName());
        dto.setSubtitle(marketItem.getSubtitle());
        dto.setDescription(marketItem.getDescription());
        dto.setPrice(marketItem.getPrice());
        dto.setCategory(marketItem.getCategory().getName());
        dto.setRating(marketItem.getRating());
        dto.setDownloads(marketItem.getDownloads());
        dto.setIconComponent(marketItem.getIconComponent());
        dto.setIconGradient(marketItem.getIconGradient());
        dto.setFeatures(marketItem.getFeatures());
        dto.setDiscount(marketItem.getDiscount());
        dto.setNew(marketItem.isNew());
        dto.setHot(marketItem.isHot());
        dto.setPremium(marketItem.isPremium());
        dto.setRarity(marketItem.getRarity());
        dto.setTags(marketItem.getTags());
        dto.setPurchased(marketItem.isPurchased());
        return dto;
    }

    public MarketItemEntity toEntity(MarketItemCreateDTO createDTO, CategoryEntity category) {
        MarketItemEntity item = new MarketItemEntity();
        item.setName(createDTO.getName());
        item.setSubtitle(createDTO.getSubtitle());
        item.setDescription(createDTO.getDescription());
        item.setPrice(createDTO.getPrice());
        item.setCategory(category);
        item.setRating(createDTO.getRating());
        item.setDownloads(createDTO.getDownloads());
        item.setIconComponent(createDTO.getIconComponent());
        item.setIconGradient(createDTO.getIconGradient());
        item.setFeatures(createDTO.getFeatures());
        item.setDiscount(createDTO.getDiscount());
        item.setNew(createDTO.isNew());
        item.setHot(createDTO.isHot());
        item.setPremium(createDTO.isPremium());
        item.setRarity(createDTO.getRarity());
        item.setTags(createDTO.getTags());
        item.setPurchased(createDTO.isPurchased());
        return item;
    }
}