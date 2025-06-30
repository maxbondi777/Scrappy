package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.entity.MarketItem;
import com.scrappy.scrappy.repository.MarketItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market/items")
@CrossOrigin(origins = {"http://localhost:5173", "https://localhost:5173", "https://192.168.1.186:5173", "http://192.168.1.186:5173"})
public class MarketController {

    @Autowired
    private MarketItemRepository marketItemRepository;

    @GetMapping
    public ResponseEntity<List<MarketItem>> getAllItems() {
        return ResponseEntity.ok(marketItemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketItem> getItemById(@PathVariable Long id) {
        return marketItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<MarketItem>> getItemsByCategory(@PathVariable String category) {
        List<MarketItem> items;
        switch (category.toLowerCase()) {
            case "all":
                items = marketItemRepository.findAll();
                break;
            case "purchased":
                items = marketItemRepository.findByPurchasedTrue();
                break;
            case "themes":
                items = marketItemRepository.findByCategory("themes");
                break;
            case "premium":
                items = marketItemRepository.findByIsPremiumTrue();
                break;
            case "productivity":
                items = marketItemRepository.findByCategory("productivity");
                break;
            default:
                items = marketItemRepository.findByCategory(category);
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MarketItem>> searchItems(@RequestParam String q) {
        List<MarketItem> items = marketItemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q);
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<MarketItem> createItem(@RequestBody MarketItem item) {
        if (item.getId() != null && marketItemRepository.existsById(item.getId())) {
            return ResponseEntity.badRequest().build();
        }
        MarketItem savedItem = marketItemRepository.save(item);
        return ResponseEntity.status(201).body(savedItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarketItem> updateItem(@PathVariable Long id, @RequestBody MarketItem item) {
        return marketItemRepository.findById(id)
                .map(existingItem -> {
                    existingItem.setName(item.getName());
                    existingItem.setSubtitle(item.getSubtitle());
                    existingItem.setDescription(item.getDescription());
                    existingItem.setPrice(item.getPrice());
                    existingItem.setCategory(item.getCategory());
                    existingItem.setRating(item.getRating());
                    existingItem.setDownloads(item.getDownloads());
                    existingItem.setIconComponent(item.getIconComponent());
                    existingItem.setIconGradient(item.getIconGradient());
                    existingItem.setFeatures(item.getFeatures());
                    existingItem.setDiscount(item.getDiscount());
                    existingItem.setIsNew(item.getIsNew()); // Исправлено
                    existingItem.setIsHot(item.getIsHot()); // Исправлено
                    existingItem.setIsPremium(item.getIsPremium()); // Исправлено
                    existingItem.setRarity(item.getRarity());
                    existingItem.setTags(item.getTags());
                    existingItem.setPurchased(item.getPurchased()); // Исправлено
                    return ResponseEntity.ok(marketItemRepository.save(existingItem));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (marketItemRepository.existsById(id)) {
            marketItemRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}