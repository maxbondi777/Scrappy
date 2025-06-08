package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.controller.dto.ApiResponse;
import com.scrappy.scrappy.controller.dto.MarketItemCreateDTO;
import com.scrappy.scrappy.controller.dto.MarketItemDTO;
import com.scrappy.scrappy.service.MarketItemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market/items")
public class MarketItemController {

    private static final Logger logger = LoggerFactory.getLogger(MarketItemController.class);
    private final MarketItemService marketItemService;

    public MarketItemController(MarketItemService marketItemService) {
        this.marketItemService = marketItemService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<MarketItemDTO>> createMarketItem(@Valid @RequestBody MarketItemCreateDTO createDTO) {
        logger.debug("Received POST /api/market/items with DTO: {}", createDTO);
        MarketItemDTO itemDTO = marketItemService.createMarketItem(createDTO);
        ApiResponse<MarketItemDTO> response = new ApiResponse<>(itemDTO, null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<MarketItemDTO>>> getAllMarketItems() {
        logger.debug("Received GET /api/market/items");
        List<MarketItemDTO> items = marketItemService.getAllMarketItems();
        ApiResponse<List<MarketItemDTO>> response = new ApiResponse<>(items, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<MarketItemDTO>> getMarketItemById(@PathVariable Long id) {
        logger.debug("Received GET /api/market/items/{}", id);
        MarketItemDTO item = marketItemService.getMarketItemById(id);
        ApiResponse<MarketItemDTO> response = new ApiResponse<>(item, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/category/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<MarketItemDTO>>> getMarketItemsByCategory(@PathVariable String category) {
        logger.debug("Received GET /api/market/items/category/{}", category);
        List<MarketItemDTO> items = marketItemService.getMarketItemsByCategory(category);
        ApiResponse<List<MarketItemDTO>> response = new ApiResponse<>(items, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<MarketItemDTO>>> searchMarketItems(@RequestParam("q") String query) {
        logger.debug("Received GET /api/market/items/search?q={}", query);
        List<MarketItemDTO> items = marketItemService.searchMarketItems(query);
        ApiResponse<List<MarketItemDTO>> response = new ApiResponse<>(items, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}