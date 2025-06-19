package com.scrappy.scrappy.service.market;

import com.scrappy.scrappy.controller.dto.market.MarketItemCreateDTO;
import com.scrappy.scrappy.controller.dto.market.MarketItemDTO;
import com.scrappy.scrappy.domain.CategoryEntity;
import com.scrappy.scrappy.domain.MarketItemEntity;
import com.scrappy.scrappy.domain.UserEntity;
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
    public MarketItemDTO createMarketItem(MarketItemCreateDTO createDTO, Long userId) {
        logger.debug("Creating market item with DTO: {}, userId: {}", createDTO, userId);
        CategoryEntity category = categoryRepository.findByName(createDTO.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + createDTO.getCategory()));
        MarketItemEntity item = marketItemMapper.toEntity(createDTO, category);
        UserEntity user = new UserEntity(); // Создаем временный объект пользователя
        user.setId(userId); // Устанавливаем ID пользователя
        item.setUser(user); // Привязываем пользователя
        MarketItemEntity savedItem = marketItemRepository.save(item);
        return marketItemMapper.toDto(savedItem);
    }

    @Transactional(readOnly = true)
    public List<MarketItemDTO> getAllMarketItems(Long userId) {
        logger.debug("Fetching all market items for userId: {}", userId);
        return marketItemRepository.findAll().stream()
                .filter(item -> item.getUser() == null || item.getUser().getId().equals(userId))
                .map(marketItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MarketItemDTO getMarketItemById(Long id, Long userId) {
        logger.debug("Fetching market item with id: {}, userId: {}", id, userId);
        return marketItemRepository.findById(id)
                .filter(item -> item.getUser() == null || item.getUser().getId().equals(userId))
                .map(marketItemMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Market item not found or access denied"));
    }

    @Transactional(readOnly = true)
    public List<MarketItemDTO> getMarketItemsByCategory(String category, Long userId) {
        logger.debug("Fetching market items for category: {}, userId: {}", category, userId);
        List<MarketItemDTO> items;
        switch (category.toLowerCase()) {
            case "all":
                items = getAllMarketItems(userId);
                break;
            case "purchased":
                items = marketItemRepository.findByPurchasedTrueAndUserId(userId).stream()
                        .filter(item -> item.getUser().getId().equals(userId))
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
            case "themes":
                items = marketItemRepository.findByCategoryNameAndUserId("themes", userId).stream()
                        .filter(item -> item.getUser().getId().equals(userId))
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
            case "premium":
                items = marketItemRepository.findByIsPremiumTrueAndUserId(userId).stream()
                        .filter(item -> item.getUser().getId().equals(userId))
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
            case "productivity":
                items = marketItemRepository.findByCategoryNameAndUserId("productivity", userId).stream()
                        .filter(item -> item.getUser().getId().equals(userId))
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }
        return items;
    }

    @Transactional(readOnly = true)
    public List<MarketItemDTO> searchMarketItems(String query, Long userId) {
        logger.debug("Searching market items with query: {}, userId: {}", query, userId);
        return marketItemRepository.searchByNameOrDescription(query).stream()
                .filter(item -> item.getUser() == null || item.getUser().getId().equals(userId))
                .map(marketItemMapper::toDto)
                .collect(Collectors.toList());
    }
}