package com.example.myapp.service;

import com.example.myapp.dto.PortfolioHolding;
import com.example.myapp.dto.PortfolioSummary;
import com.example.myapp.model.*;
import com.example.myapp.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

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

    private Portfolio testPortfolio;
    private User testUser;
    private Stock testStock1;
    private Stock testStock2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testPortfolio = new Portfolio();
        testPortfolio.setId(1L);
        testPortfolio.setUser(testUser);

        testStock1 = new Stock();
        testStock1.setId(1L);
        testStock1.setSymbol("AAPL");
        testStock1.setName("Apple Inc.");
        testStock1.setCurrentPrice(new BigDecimal("150.00"));
        testStock1.setPreviousClose(new BigDecimal("148.00"));
        testStock1.setDayChange(new BigDecimal("2.00"));
        testStock1.setDayChangePercent(new BigDecimal("1.35"));

        testStock2 = new Stock();
        testStock2.setId(2L);
        testStock2.setSymbol("GOOGL");
        testStock2.setName("Alphabet Inc.");
        testStock2.setCurrentPrice(new BigDecimal("2800.00"));
        testStock2.setPreviousClose(new BigDecimal("2750.00"));
        testStock2.setDayChange(new BigDecimal("50.00"));
        testStock2.setDayChangePercent(new BigDecimal("1.82"));
    }

    @Test
    void testGetPortfolioSummary_WithMultipleHoldings() {
        // Arrange
        PortfolioStock holding1 = new PortfolioStock();
        holding1.setId(1L);
        holding1.setPortfolio(testPortfolio);
        holding1.setStock(testStock1);
        holding1.setQuantity(10);
        holding1.setAverageBuyPrice(new BigDecimal("140.00"));

        PortfolioStock holding2 = new PortfolioStock();
        holding2.setId(2L);
        holding2.setPortfolio(testPortfolio);
        holding2.setStock(testStock2);
        holding2.setQuantity(5);
        holding2.setAverageBuyPrice(new BigDecimal("2700.00"));

        List<PortfolioStock> holdings = Arrays.asList(holding1, holding2);

        when(portfolioRepo.findByUserId(1L)).thenReturn(Optional.of(testPortfolio));
        when(portfolioStockRepo.findByPortfolioId(1L)).thenReturn(holdings);

        // Act
        PortfolioSummary summary = portfolioService.getPortfolioSummary(1L);

        // Assert
        assertNotNull(summary);
        assertEquals(2, summary.getHoldings().size());

        // Total value = (10 * 150) + (5 * 2800) = 1500 + 14000 = 15500
        assertEquals(new BigDecimal("15500.00"), summary.getTotalValue());

        // Total cost = (10 * 140) + (5 * 2700) = 1400 + 13500 = 14900
        assertEquals(new BigDecimal("14900.00"), summary.getTotalCost());

        // Total gain/loss = 15500 - 14900 = 600
        assertEquals(new BigDecimal("600.00"), summary.getTotalGainLoss());

        // Day change = (10 * 2) + (5 * 50) = 20 + 250 = 270
        assertEquals(new BigDecimal("270.00"), summary.getDayChange());

        verify(portfolioRepo).findByUserId(1L);
        verify(portfolioStockRepo).findByPortfolioId(1L);
    }

    @Test
    void testGetPortfolioSummary_WithEmptyPortfolio() {
        // Arrange
        when(portfolioRepo.findByUserId(1L)).thenReturn(Optional.of(testPortfolio));
        when(portfolioStockRepo.findByPortfolioId(1L)).thenReturn(Arrays.asList());

        // Act
        PortfolioSummary summary = portfolioService.getPortfolioSummary(1L);

        // Assert
        assertNotNull(summary);
        assertEquals(0, summary.getHoldings().size());
        assertEquals(BigDecimal.ZERO, summary.getTotalValue());
        assertEquals(BigDecimal.ZERO, summary.getTotalCost());
        assertEquals(BigDecimal.ZERO, summary.getTotalGainLoss());
        assertEquals(BigDecimal.ZERO, summary.getDayChange());
    }

    @Test
    void testGetPortfolioSummary_CalculatesHoldingMetrics() {
        // Arrange
        PortfolioStock holding = new PortfolioStock();
        holding.setId(1L);
        holding.setPortfolio(testPortfolio);
        holding.setStock(testStock1);
        holding.setQuantity(10);
        holding.setAverageBuyPrice(new BigDecimal("140.00"));

        when(portfolioRepo.findByUserId(1L)).thenReturn(Optional.of(testPortfolio));
        when(portfolioStockRepo.findByPortfolioId(1L)).thenReturn(Arrays.asList(holding));

        // Act
        PortfolioSummary summary = portfolioService.getPortfolioSummary(1L);

        // Assert
        assertNotNull(summary);
        assertEquals(1, summary.getHoldings().size());

        PortfolioHolding holdingDto = summary.getHoldings().get(0);
        assertEquals("AAPL", holdingDto.getStock().getSymbol());
        assertEquals(10, holdingDto.getQuantity());
        assertEquals(new BigDecimal("140.00"), holdingDto.getAverageCost());
        
        // Current value = 10 * 150 = 1500
        assertEquals(new BigDecimal("1500.00"), holdingDto.getCurrentValue());
        
        // Gain/loss = 1500 - 1400 = 100
        assertEquals(new BigDecimal("100.00"), holdingDto.getGainLoss());
    }

    @Test
    void testGetPortfolioSummary_HandlesNullDayChange() {
        // Arrange
        testStock1.setDayChange(null);
        
        PortfolioStock holding = new PortfolioStock();
        holding.setId(1L);
        holding.setPortfolio(testPortfolio);
        holding.setStock(testStock1);
        holding.setQuantity(10);
        holding.setAverageBuyPrice(new BigDecimal("140.00"));

        when(portfolioRepo.findByUserId(1L)).thenReturn(Optional.of(testPortfolio));
        when(portfolioStockRepo.findByPortfolioId(1L)).thenReturn(Arrays.asList(holding));

        // Act
        PortfolioSummary summary = portfolioService.getPortfolioSummary(1L);

        // Assert
        assertNotNull(summary);
        assertEquals(BigDecimal.ZERO, summary.getDayChange());
    }
}
