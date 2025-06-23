package com.scrappy.scrappy.controller;

import com.scrappy.scrappy.controller.dto.ApiResponse;
import com.scrappy.scrappy.controller.dto.market.MarketItemCreateDTO;
import com.scrappy.scrappy.controller.dto.market.MarketItemDTO;
import com.scrappy.scrappy.service.market.MarketItemService;
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
@CrossOrigin(origins = {"http://localhost:5173", "https://localhost:5173", "https://192.168.1.186:5173", "http://192.168.1.186:5173"}, allowCredentials = "true")
public class MarketItemController {

    private static final Logger logger = LoggerFactory.getLogger(MarketItemController.class);
    private final MarketItemService marketItemService;

    public MarketItemController(MarketItemService marketItemService) {
        this.marketItemService = marketItemService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<MarketItemDTO>> createMarketItem(@Valid @RequestBody MarketItemCreateDTO createDTO,
                                                                       @RequestHeader("X-User-Id") Long userId) {
        MarketItemDTO itemDTO = marketItemService.createMarketItem(createDTO, userId);
        ApiResponse<MarketItemDTO> response = new ApiResponse<>(itemDTO, null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<MarketItemDTO>>> getAllMarketItems(@RequestHeader("X-User-Id") Long userId) {
        List<MarketItemDTO> items = marketItemService.getAllMarketItems(userId);
        ApiResponse<List<MarketItemDTO>> response = new ApiResponse<>(items, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<MarketItemDTO>> getMarketItemById(@PathVariable Long id,
                                                                        @RequestHeader("X-User-Id") Long userId) {
        MarketItemDTO item = marketItemService.getMarketItemById(id, userId);
        ApiResponse<MarketItemDTO> response = new ApiResponse<>(item, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/category/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<MarketItemDTO>>> getMarketItemsByCategory(@PathVariable String category,
                                                                                     @RequestHeader("X-User-Id") Long userId) {
        List<MarketItemDTO> items = marketItemService.getMarketItemsByCategory(category, userId);
        ApiResponse<List<MarketItemDTO>> response = new ApiResponse<>(items, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<MarketItemDTO>>> searchMarketItems(@RequestParam("q") String query,
                                                                              @RequestHeader("X-User-Id") Long userId) {
        List<MarketItemDTO> items = marketItemService.searchMarketItems(query, userId);
        ApiResponse<List<MarketItemDTO>> response = new ApiResponse<>(items, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}