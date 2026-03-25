package com.example.myapp.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.myapp.dto.PortfolioSummary;
import com.example.myapp.model.PortfolioStock;
import com.example.myapp.model.Transaction;
import com.example.myapp.service.PortfolioService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/{userId}")
    public List<PortfolioStock> getHoldings(@PathVariable Long userId) {
        return portfolioService.getHoldings(userId);
    }

    @PostMapping("/{userId}/buy")
    public ResponseEntity<Transaction> buyStock(
            @PathVariable Long userId,
            @RequestParam String symbol,
            @RequestParam int quantity,
            @RequestParam BigDecimal price) {
        Transaction tx = portfolioService.buyStock(userId, symbol.toUpperCase(), quantity, price);
        return ResponseEntity.status(HttpStatus.CREATED).body(tx);
    }

    @PostMapping("/{userId}/sell")
    public ResponseEntity<Transaction> sellStock(
            @PathVariable Long userId,
            @RequestParam String symbol,
            @RequestParam int quantity,
            @RequestParam BigDecimal price) {
        Transaction tx = portfolioService.sellStock(userId, symbol.toUpperCase(), quantity, price);
        return ResponseEntity.ok(tx);
    }

    @GetMapping("/{userId}/transactions")
    public List<Transaction> getTransactions(@PathVariable Long userId) {
        return portfolioService.getTransactions(userId);
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<PortfolioSummary> getPortfolioSummary(@PathVariable Long userId) {
        PortfolioSummary summary = portfolioService.getPortfolioSummary(userId);
        return ResponseEntity.ok(summary);
    }
}
