package com.example.myapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myapp.dto.PortfolioHolding;
import com.example.myapp.dto.PortfolioSummary;
import com.example.myapp.model.*;
import com.example.myapp.repo.*;
import com.example.myapp.exception.ValidationException;
import com.example.myapp.exception.InsufficientQuantityException;
import com.example.myapp.exception.StockNotFoundException;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepo;
    private final PortfolioStockRepository portfolioStockRepo;
    private final TransactionRepository transactionRepo;
    private final StockService stockService;
    private final UserService userService;

    public PortfolioService(PortfolioRepository portfolioRepo,
                            PortfolioStockRepository portfolioStockRepo,
                            TransactionRepository transactionRepo,
                            StockService stockService,
                            UserService userService) {
        this.portfolioRepo = portfolioRepo;
        this.portfolioStockRepo = portfolioStockRepo;
        this.transactionRepo = transactionRepo;
        this.stockService = stockService;
        this.userService = userService;
    }

    /**
     * Validate transaction inputs.
     */
    private void validateTransaction(String symbol, int quantity, BigDecimal price) {
        // Validate symbol
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new ValidationException("Stock symbol cannot be empty");
        }
        
        String trimmedSymbol = symbol.trim().toUpperCase();
        if (!trimmedSymbol.matches("^[A-Z]{1,5}$")) {
            throw new ValidationException("Invalid stock symbol format. Symbol must be 1-5 uppercase letters.");
        }
        
        // Validate quantity
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be a positive number");
        }
        
        if (quantity > 1000000) {
            throw new ValidationException("Quantity exceeds maximum allowed value of 1,000,000");
        }
        
        // Validate price
        if (price == null) {
            throw new ValidationException("Price cannot be null");
        }
        
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be a positive number");
        }
        
        if (price.compareTo(new BigDecimal("1000000")) > 0) {
            throw new ValidationException("Price exceeds maximum allowed value of $1,000,000");
        }
        
        // Validate price has reasonable decimal places (max 2)
        if (price.scale() > 2) {
            throw new ValidationException("Price can have at most 2 decimal places");
        }
    }

    /**
     * Get or create a portfolio for a user.
     */
    public Portfolio getOrCreatePortfolio(Long userId) {
        return portfolioRepo.findByUserId(userId)
                .orElseGet(() -> {
                    Portfolio p = new Portfolio();
                    p.setUser(userService.getUserById(userId));
                    return portfolioRepo.save(p);
                });
    }

    /**
     * Buy stock: add to holdings and record transaction.
     */
    @Transactional
    public Transaction buyStock(Long userId, String symbol, int quantity, BigDecimal price) {
        // Validate inputs
        validateTransaction(symbol, quantity, price);
        
        Portfolio portfolio = getOrCreatePortfolio(userId);
        Stock stock;
        
        try {
            stock = stockService.getStockBySymbol(symbol.trim().toUpperCase());
        } catch (RuntimeException e) {
            throw new StockNotFoundException("Stock not found: " + symbol);
        }

        // Update or create holding
        Optional<PortfolioStock> existingHolding =
                portfolioStockRepo.findByPortfolioIdAndStockId(portfolio.getId(), stock.getId());

        if (existingHolding.isPresent()) {
            PortfolioStock holding = existingHolding.get();
            // Calculate new weighted average buy price
            BigDecimal totalOldCost = holding.getAverageBuyPrice()
                    .multiply(BigDecimal.valueOf(holding.getQuantity()));
            BigDecimal totalNewCost = price.multiply(BigDecimal.valueOf(quantity));
            int totalQuantity = holding.getQuantity() + quantity;
            BigDecimal newAvg = totalOldCost.add(totalNewCost)
                    .divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP);

            holding.setQuantity(totalQuantity);
            holding.setAverageBuyPrice(newAvg);
            portfolioStockRepo.save(holding);
        } else {
            PortfolioStock newHolding = new PortfolioStock();
            newHolding.setPortfolio(portfolio);
            newHolding.setStock(stock);
            newHolding.setQuantity(quantity);
            newHolding.setAverageBuyPrice(price);
            portfolioStockRepo.save(newHolding);
        }

        // Record transaction
        Transaction tx = new Transaction();
        tx.setPortfolio(portfolio);
        tx.setStock(stock);
        tx.setType(TransactionType.BUY);
        tx.setQuantity(quantity);
        tx.setPrice(price);
        return transactionRepo.save(tx);
    }

    /**
     * Sell stock: reduce holdings and record transaction.
     */
    @Transactional
    public Transaction sellStock(Long userId, String symbol, int quantity, BigDecimal price) {
        // Validate inputs
        validateTransaction(symbol, quantity, price);
        
        Portfolio portfolio = getOrCreatePortfolio(userId);
        Stock stock;
        
        try {
            stock = stockService.getStockBySymbol(symbol.trim().toUpperCase());
        } catch (RuntimeException e) {
            throw new StockNotFoundException("Stock not found: " + symbol);
        }

        PortfolioStock holding = portfolioStockRepo
                .findByPortfolioIdAndStockId(portfolio.getId(), stock.getId())
                .orElseThrow(() -> new InsufficientQuantityException("You don't own this stock"));

        if (holding.getQuantity() < quantity) {
            throw new InsufficientQuantityException(
                "Cannot sell " + quantity + " shares. You only own " + holding.getQuantity() + " shares."
            );
        }

        int remaining = holding.getQuantity() - quantity;
        if (remaining == 0) {
            portfolioStockRepo.delete(holding);
        } else {
            holding.setQuantity(remaining);
            portfolioStockRepo.save(holding);
        }

        // Record transaction
        Transaction tx = new Transaction();
        tx.setPortfolio(portfolio);
        tx.setStock(stock);
        tx.setType(TransactionType.SELL);
        tx.setQuantity(quantity);
        tx.setPrice(price);
        return transactionRepo.save(tx);
    }

    /**
     * Get all holdings in a user's portfolio.
     */
    public List<PortfolioStock> getHoldings(Long userId) {
        Portfolio portfolio = getOrCreatePortfolio(userId);
        return portfolioStockRepo.findByPortfolioId(portfolio.getId());
    }

    /**
     * Get transaction history for a user.
     */
    public List<Transaction> getTransactions(Long userId) {
        Portfolio portfolio = getOrCreatePortfolio(userId);
        return transactionRepo.findByPortfolioId(portfolio.getId());
    }

    /**
     * Calculate portfolio summary with total value, P&L, and daily changes.
     */
    public PortfolioSummary getPortfolioSummary(Long userId) {
        Portfolio portfolio = getOrCreatePortfolio(userId);
        List<PortfolioStock> holdings = portfolioStockRepo.findByPortfolioId(portfolio.getId());

        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal dayChange = BigDecimal.ZERO;
        List<PortfolioHolding> holdingDtos = new ArrayList<>();

        for (PortfolioStock holding : holdings) {
            Stock stock = holding.getStock();
            Integer quantity = holding.getQuantity();
            BigDecimal averageCost = holding.getAverageBuyPrice();

            // Calculate current value
            BigDecimal currentValue = stock.getCurrentPrice()
                    .multiply(BigDecimal.valueOf(quantity));

            // Calculate cost basis
            BigDecimal costBasis = averageCost.multiply(BigDecimal.valueOf(quantity));

            // Calculate gain/loss
            BigDecimal gainLoss = currentValue.subtract(costBasis);
            BigDecimal gainLossPercent = costBasis.compareTo(BigDecimal.ZERO) > 0
                    ? gainLoss.divide(costBasis, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;

            // Calculate daily change for this holding
            BigDecimal holdingDayChange = stock.getDayChange() != null
                    ? stock.getDayChange().multiply(BigDecimal.valueOf(quantity))
                    : BigDecimal.ZERO;

            // Accumulate totals
            totalValue = totalValue.add(currentValue);
            totalCost = totalCost.add(costBasis);
            dayChange = dayChange.add(holdingDayChange);

            // Create holding DTO
            PortfolioHolding holdingDto = new PortfolioHolding(
                    stock,
                    quantity,
                    averageCost,
                    currentValue,
                    gainLoss,
                    gainLossPercent
            );
            holdingDtos.add(holdingDto);
        }

        // Calculate total gain/loss
        BigDecimal totalGainLoss = totalValue.subtract(totalCost);
        BigDecimal totalGainLossPercent = totalCost.compareTo(BigDecimal.ZERO) > 0
                ? totalGainLoss.divide(totalCost, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        // Calculate day change percentage
        BigDecimal previousTotalValue = totalValue.subtract(dayChange);
        BigDecimal dayChangePercent = previousTotalValue.compareTo(BigDecimal.ZERO) > 0
                ? dayChange.divide(previousTotalValue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return new PortfolioSummary(
                totalValue,
                totalCost,
                totalGainLoss,
                totalGainLossPercent,
                dayChange,
                dayChangePercent,
                holdingDtos
        );
    }
}
