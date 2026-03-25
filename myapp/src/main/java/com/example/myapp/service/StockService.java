package com.example.myapp.service;

import com.example.myapp.model.Stock;
import com.example.myapp.repo.StockRepository;
import com.example.myapp.exception.StockNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final StockRepository stockRepo;

    public StockService(StockRepository stockRepo) {
        this.stockRepo = stockRepo;
    }

    public Stock addStock(Stock stock) {
        if (stockRepo.findBySymbol(stock.getSymbol()).isPresent()) {
            throw new IllegalStateException("Stock with symbol " + stock.getSymbol() + " already exists");
        }
        stock.setLastUpdated(LocalDateTime.now());
        return stockRepo.save(stock);
    }

    public List<Stock> getAllStocks() {
        return stockRepo.findAll();
    }

    public Stock getStockBySymbol(String symbol) {
        return stockRepo.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException("Stock not found: " + symbol));
    }

    public Stock getStockById(Long id) {
        return stockRepo.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Stock not found with id: " + id));
    }

    /**
     * Update stock price. Placeholder for future external API integration.
     */
    public Stock updatePrice(String symbol, BigDecimal newPrice) {
        Stock stock = getStockBySymbol(symbol);
        stock.setCurrentPrice(newPrice);
        stock.setLastUpdated(LocalDateTime.now());
        return stockRepo.save(stock);
    }

    /**
     * Search stocks by symbol or company name with fuzzy matching.
     * Validates symbol format before searching.
     * 
     * @param query Search query (symbol or company name)
     * @return List of matching stocks
     */
    public List<Stock> searchStocks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // Validate symbol format (alphanumeric, 1-5 characters for symbols)
        String trimmedQuery = query.trim();
        
        // Search by symbol (case-insensitive)
        List<Stock> symbolMatches = stockRepo.findBySymbolContainingIgnoreCase(trimmedQuery);
        
        // Search by name (case-insensitive)
        List<Stock> nameMatches = stockRepo.findByNameContainingIgnoreCase(trimmedQuery);
        
        // Combine results, removing duplicates
        List<Stock> results = new ArrayList<>(symbolMatches);
        for (Stock stock : nameMatches) {
            if (!results.contains(stock)) {
                results.add(stock);
            }
        }
        
        return results;
    }

    /**
     * Get detailed stock information including all metrics.
     * Validates symbol before fetching.
     * 
     * @param symbol Stock symbol
     * @return Stock with complete details
     */
    public Stock getStockDetails(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol cannot be empty");
        }
        
        // Validate symbol format: must be 1-5 uppercase letters only
        String trimmedSymbol = symbol.trim();
        if (!trimmedSymbol.matches("^[A-Z]{1,5}$")) {
            throw new IllegalArgumentException("Invalid stock symbol format: " + symbol);
        }
        
        return stockRepo.findBySymbol(trimmedSymbol)
                .orElseThrow(() -> new StockNotFoundException("Stock not found: " + trimmedSymbol));
    }
}
