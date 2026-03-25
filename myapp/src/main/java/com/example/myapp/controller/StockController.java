package com.example.myapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.myapp.model.Stock;
import com.example.myapp.service.StockService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<Stock> addStock(@RequestBody Stock stock) {
        Stock saved = stockService.addStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    @GetMapping("/{symbol}")
    public Stock getStockBySymbol(@PathVariable String symbol) {
        return stockService.getStockBySymbol(symbol.toUpperCase());
    }

    /**
     * Search stocks by symbol or company name.
     * Supports fuzzy matching for better UX.
     * 
     * @param query Search query parameter
     * @return List of matching stocks
     */
    @GetMapping("/search")
    public ResponseEntity<List<Stock>> searchStocks(@RequestParam String query) {
        List<Stock> results = stockService.searchStocks(query);
        return ResponseEntity.ok(results);
    }

    /**
     * Get detailed stock information including all metrics.
     * 
     * @param symbol Stock symbol
     * @return Detailed stock information
     */
    @GetMapping("/{symbol}/details")
    public ResponseEntity<Stock> getStockDetails(@PathVariable String symbol) {
        Stock stock = stockService.getStockDetails(symbol);
        return ResponseEntity.ok(stock);
    }
}
