package com.scrappy.scrappy.service.market;

import com.scrappy.scrappy.controller.dto.market.MarketItemCreateDTO;
import com.scrappy.scrappy.controller.dto.market.MarketItemDTO;
import com.scrappy.scrappy.domain.CategoryEntity;
import com.scrappy.scrappy.domain.MarketItemEntity;
import com.scrappy.scrappy.domain.UserEntity;
import com.scrappy.scrappy.repository.CategoryRepository;
import com.scrappy.scrappy.repository.MarketItemRepository;
import com.scrappy.scrappy.repository.UserRepository;
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
    private final UserRepository userRepository;

    public MarketItemService(MarketItemRepository marketItemRepository, CategoryRepository categoryRepository,
                             MarketItemMapper marketItemMapper, UserRepository userRepository) {
        this.marketItemRepository = marketItemRepository;
        this.categoryRepository = categoryRepository;
        this.marketItemMapper = marketItemMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public MarketItemDTO createMarketItem(MarketItemCreateDTO createDTO, Long userId) {
        logger.debug("Creating market item with DTO: {}, userId: {}", createDTO, userId);
        CategoryEntity category = categoryRepository.findByName(createDTO.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + createDTO.getCategory()));
        UserEntity user = userRepository.findByTelegramId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + userId));
        MarketItemEntity item = marketItemMapper.toEntity(createDTO, category);
        item.setUser(user);
        MarketItemEntity savedItem = marketItemRepository.save(item);
        return marketItemMapper.toDto(savedItem);
    }

    @Transactional(readOnly = true)
    public List<MarketItemDTO> getAllMarketItems(Long userId) {
        logger.debug("Fetching all market items for userId: {}", userId);
        UserEntity user = userRepository.findByTelegramId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + userId));
        return marketItemRepository.findAll().stream()
                .filter(item -> item.getUser() == null || item.getUser().getId().equals(user.getId()))
                .map(marketItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MarketItemDTO getMarketItemById(Long id, Long userId) {
        logger.debug("Fetching market item with id: {}, userId: {}", id, userId);
        UserEntity user = userRepository.findByTelegramId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + userId));
        return marketItemRepository.findById(id)
                .filter(item -> item.getUser() == null || item.getUser().getId().equals(user.getId()))
                .map(marketItemMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Market item not found or access denied"));
    }

    @Transactional(readOnly = true)
    public List<MarketItemDTO> getMarketItemsByCategory(String category, Long userId) {
        logger.debug("Fetching market items for category: {}, userId: {}", category, userId);
        UserEntity user = userRepository.findByTelegramId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + userId));
        List<MarketItemDTO> items;
        switch (category.toLowerCase()) {
            case "all":
                items = getAllMarketItems(userId);
                break;
            case "purchased":
                items = marketItemRepository.findByPurchasedTrueAndUserId(user.getId()).stream()
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
            case "themes":
                items = marketItemRepository.findByCategoryNameAndUserId("themes", user.getId()).stream()
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
            case "premium":
                items = marketItemRepository.findByIsPremiumTrueAndUserId(user.getId()).stream()
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
            case "productivity":
                items = marketItemRepository.findByCategoryNameAndUserId("productivity", user.getId()).stream()
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
            default:
                items = marketItemRepository.findByCategoryNameAndUserId(category, user.getId()).stream()
                        .map(marketItemMapper::toDto)
                        .collect(Collectors.toList());
                break;
        }
        return items;
    }

    @Transactional(readOnly = true)
    public List<MarketItemDTO> searchMarketItems(String query, Long userId) {
        logger.debug("Searching market items with query: {}, userId: {}", query, userId);
        UserEntity user = userRepository.findByTelegramId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with telegramId: " + userId));
        return marketItemRepository.searchByNameOrDescription(query).stream()
                .filter(item -> item.getUser() == null || item.getUser().getId().equals(user.getId()))
                .map(marketItemMapper::toDto)
                .collect(Collectors.toList());
    }
}