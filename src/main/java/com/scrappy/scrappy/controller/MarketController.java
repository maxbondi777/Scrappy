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
        return ResponseEntity.ok(marketItemRepository.findByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MarketItem>> searchItems(@RequestParam String q) {
        return ResponseEntity.ok(marketItemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q));
    }
}