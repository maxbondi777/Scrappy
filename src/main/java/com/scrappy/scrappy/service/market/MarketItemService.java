package com.scrappy.scrappy.service.market;

import com.scrappy.scrappy.controller.dto.market.MarketItemCreateDTO;
import com.scrappy.scrappy.controller.dto.market.MarketItemDTO;
import com.scrappy.scrappy.domain.CategoryEntity;
import com.scrappy.scrappy.domain.MarketItemEntity;
import com.scrappy.scrappy.repository.CategoryRepository;
import com.scrappy.scrappy.repository.MarketItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarketItemService {

    private static final Logger logger = LoggerFactory.getLogger(MarketItemService.class);
    private final MarketItemRepository marketItemRepository;
    private final CategoryRepository categoryRepository;
    private final MarketItemMapper marketItemMapper;

    public MarketItemService(MarketItemRepository marketItemRepository, CategoryRepository categoryRepository, MarketItemMapper marketItemMapper) {
        this.marketItemRepository = marketItemRepository;
        this.categoryRepository = categoryRepository;
        this.marketItemMapper = marketItemMapper;
    }

    @Transactional
    public MarketItemDTO createMarketItem(MarketItemCreateDTO createDTO) {
        logger.debug("Creating market item with DTO: {}", createDTO);
        CategoryEntity category = categoryRepository.findByName(createDTO.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + createDTO.getCategory()));
        MarketItemEntity item = marketItemMapper.toEntity(createDTO, category);
        MarketItemEntity savedItem = marketItemRepository.save(item);
        return marketItemMapper.toDto(savedItem);
    }

    @Transactional(readOnly = true)
    public List<MarketItemDTO> getAllMarketItems() {
        logger.debug("Fetching all market items");
        return marketItemRepository.findAll().stream()
                .map(marketItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MarketItemDTO getMarketItemById(Long id) {
        logger.debug("Fetching market item with id: {}", id);
        return marketItemRepository.findById(id)
                .map(marketItemMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Market item not found"));
    }

    @Transactional(readOnly = true)
    public List<MarketItemDTO> getMarketItemsByCategory(String category) {
        logger.debug("Fetching market items for category: {}", category);
        List<MarketItemDTO> items;
        switch (category.toLowerCase()) {
            case "all":
                items = getAllMarketItems();
                break;
            case "purchased":
                items = marketItemRepository.findByPurchasedTrue().stream()
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
            case "themes":
                items = marketItemRepository.findByCategoryName("themes").stream()
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
            case "premium":
                items = marketItemRepository.findByIsPremiumTrue().stream()
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
            case "productivity":
                items = marketItemRepository.findByCategoryName("productivity").stream()
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }
        return items;
    }

    @Transactional(readOnly = true)
    public List<MarketItemDTO> searchMarketItems(String query) {
        logger.debug("Searching market items with query: {}", query);
        return marketItemRepository.searchByNameOrDescription(query).stream()
                .map(marketItemMapper::toDto)
                .collect(Collectors.toList());
    }
}