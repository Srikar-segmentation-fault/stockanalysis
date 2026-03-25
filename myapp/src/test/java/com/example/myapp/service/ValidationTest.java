package com.example.myapp.service;

import com.example.myapp.exception.ValidationException;
import com.example.myapp.exception.InsufficientQuantityException;
import com.example.myapp.exception.StockNotFoundException;
import com.example.myapp.model.Portfolio;
import com.example.myapp.model.Stock;
import com.example.myapp.model.User;
import com.example.myapp.repo.PortfolioRepository;
import com.example.myapp.repo.PortfolioStockRepository;
import com.example.myapp.repo.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidationTest {

    @Mock
    private PortfolioRepository portfolioRepo;

    @Mock
    private PortfolioStockRepository portfolioStockRepo;

    @Mock
    private TransactionRepository transactionRepo;

    @Mock
    private StockService stockService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PortfolioService portfolioService;

    private User testUser;
    private Portfolio testPortfolio;
    private Stock testStock;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");

        testPortfolio = new Portfolio();
        testPortfolio.setId(1L);
        testPortfolio.setUser(testUser);

        testStock = new Stock();
        testStock.setId(1L);
        testStock.setSymbol("AAPL");
        testStock.setName("Apple Inc.");
        testStock.setCurrentPrice(new BigDecimal("150.00"));
    }

    @Test
    void testBuyStock_InvalidSymbol_ThrowsValidationException() {
        // Test empty symbol
        assertThrows(ValidationException.class, () -> {
            portfolioService.buyStock(1L, "", 10, new BigDecimal("150.00"));
        });

        // Test invalid symbol format
        assertThrows(ValidationException.class, () -> {
            portfolioService.buyStock(1L, "TOOLONG", 10, new BigDecimal("150.00"));
        });
    }

    @Test
    void testBuyStock_InvalidQuantity_ThrowsValidationException() {
        // Test zero quantity
        assertThrows(ValidationException.class, () -> {
            portfolioService.buyStock(1L, "AAPL", 0, new BigDecimal("150.00"));
        });

        // Test negative quantity
        assertThrows(ValidationException.class, () -> {
            portfolioService.buyStock(1L, "AAPL", -5, new BigDecimal("150.00"));
        });

        // Test quantity exceeds maximum
        assertThrows(ValidationException.class, () -> {
            portfolioService.buyStock(1L, "AAPL", 2000000, new BigDecimal("150.00"));
        });
    }

    @Test
    void testBuyStock_InvalidPrice_ThrowsValidationException() {
        // Test zero price
        assertThrows(ValidationException.class, () -> {
            portfolioService.buyStock(1L, "AAPL", 10, BigDecimal.ZERO);
        });

        // Test negative price
        assertThrows(ValidationException.class, () -> {
            portfolioService.buyStock(1L, "AAPL", 10, new BigDecimal("-150.00"));
        });

        // Test price exceeds maximum
        assertThrows(ValidationException.class, () -> {
            portfolioService.buyStock(1L, "AAPL", 10, new BigDecimal("2000000.00"));
        });

        // Test too many decimal places
        assertThrows(ValidationException.class, () -> {
            portfolioService.buyStock(1L, "AAPL", 10, new BigDecimal("150.123"));
        });
    }

    @Test
    void testBuyStock_StockNotFound_ThrowsStockNotFoundException() {
        when(portfolioRepo.findByUserId(1L)).thenReturn(Optional.of(testPortfolio));
        when(stockService.getStockBySymbol("AAPL")).thenThrow(new RuntimeException("Stock not found"));

        assertThrows(StockNotFoundException.class, () -> {
            portfolioService.buyStock(1L, "AAPL", 10, new BigDecimal("150.00"));
        });
    }

    @Test
    void testSellStock_InsufficientQuantity_ThrowsInsufficientQuantityException() {
        when(portfolioRepo.findByUserId(1L)).thenReturn(Optional.of(testPortfolio));
        when(stockService.getStockBySymbol("AAPL")).thenReturn(testStock);
        when(portfolioStockRepo.findByPortfolioIdAndStockId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(InsufficientQuantityException.class, () -> {
            portfolioService.sellStock(1L, "AAPL", 10, new BigDecimal("150.00"));
        });
    }
}
