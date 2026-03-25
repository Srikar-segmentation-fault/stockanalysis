package com.example.myapp.controller;

import com.example.myapp.dto.PortfolioSummary;
import com.example.myapp.model.*;
import com.example.myapp.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PortfolioControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioStockRepository portfolioStockRepository;

    private User testUser;
    private Stock testStock1;
    private Stock testStock2;
    private Portfolio testPortfolio;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        portfolioStockRepository.deleteAll();
        portfolioRepository.deleteAll();
        stockRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        // Create test stocks
        testStock1 = new Stock();
        testStock1.setSymbol("AAPL");
        testStock1.setName("Apple Inc.");
        testStock1.setCurrentPrice(new BigDecimal("150.00"));
        testStock1.setPreviousClose(new BigDecimal("148.00"));
        testStock1.setDayChange(new BigDecimal("2.00"));
        testStock1.setDayChangePercent(new BigDecimal("1.35"));
        testStock1 = stockRepository.save(testStock1);

        testStock2 = new Stock();
        testStock2.setSymbol("GOOGL");
        testStock2.setName("Alphabet Inc.");
        testStock2.setCurrentPrice(new BigDecimal("2800.00"));
        testStock2.setPreviousClose(new BigDecimal("2750.00"));
        testStock2.setDayChange(new BigDecimal("50.00"));
        testStock2.setDayChangePercent(new BigDecimal("1.82"));
        testStock2 = stockRepository.save(testStock2);

        // Create test portfolio
        testPortfolio = new Portfolio();
        testPortfolio.setUser(testUser);
        testPortfolio = portfolioRepository.save(testPortfolio);

        // Add holdings to portfolio
        PortfolioStock holding1 = new PortfolioStock();
        holding1.setPortfolio(testPortfolio);
        holding1.setStock(testStock1);
        holding1.setQuantity(10);
        holding1.setAverageBuyPrice(new BigDecimal("140.00"));
        portfolioStockRepository.save(holding1);

        PortfolioStock holding2 = new PortfolioStock();
        holding2.setPortfolio(testPortfolio);
        holding2.setStock(testStock2);
        holding2.setQuantity(5);
        holding2.setAverageBuyPrice(new BigDecimal("2700.00"));
        portfolioStockRepository.save(holding2);
    }

    @Test
    void testGetPortfolioSummary_ReturnsCorrectData() {
        // Act
        String url = "http://localhost:" + port + "/api/portfolio/" + testUser.getId() + "/summary";
        ResponseEntity<PortfolioSummary> response = restTemplate.getForEntity(url, PortfolioSummary.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        PortfolioSummary summary = response.getBody();
        assertEquals(2, summary.getHoldings().size());

        // Total value = (10 * 150) + (5 * 2800) = 15500
        assertEquals(0, new BigDecimal("15500.00").compareTo(summary.getTotalValue()));

        // Total cost = (10 * 140) + (5 * 2700) = 14900
        assertEquals(0, new BigDecimal("14900.00").compareTo(summary.getTotalCost()));

        // Total gain/loss = 15500 - 14900 = 600
        assertEquals(0, new BigDecimal("600.00").compareTo(summary.getTotalGainLoss()));

        // Day change = (10 * 2) + (5 * 50) = 270
        assertEquals(0, new BigDecimal("270.00").compareTo(summary.getDayChange()));
    }

    @Test
    void testGetPortfolioSummary_EmptyPortfolio() {
        // Arrange - create a new user with no holdings
        User emptyUser = new User();
        emptyUser.setName("Empty User");
        emptyUser.setEmail("empty@example.com");
        emptyUser.setPassword("password");
        emptyUser = userRepository.save(emptyUser);

        // Act
        String url = "http://localhost:" + port + "/api/portfolio/" + emptyUser.getId() + "/summary";
        ResponseEntity<PortfolioSummary> response = restTemplate.getForEntity(url, PortfolioSummary.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        PortfolioSummary summary = response.getBody();
        assertEquals(0, summary.getHoldings().size());
        assertEquals(0, BigDecimal.ZERO.compareTo(summary.getTotalValue()));
        assertEquals(0, BigDecimal.ZERO.compareTo(summary.getTotalCost()));
    }

    @Test
    void testGetPortfolioSummary_CalculatesPercentages() {
        // Act
        String url = "http://localhost:" + port + "/api/portfolio/" + testUser.getId() + "/summary";
        ResponseEntity<PortfolioSummary> response = restTemplate.getForEntity(url, PortfolioSummary.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        PortfolioSummary summary = response.getBody();
        
        // Total gain/loss percent = (600 / 14900) * 100 ≈ 4.03%
        assertTrue(summary.getTotalGainLossPercent().compareTo(new BigDecimal("4.0")) > 0);
        assertTrue(summary.getTotalGainLossPercent().compareTo(new BigDecimal("4.1")) < 0);

        // Day change percent should be calculated
        assertNotNull(summary.getDayChangePercent());
        assertTrue(summary.getDayChangePercent().compareTo(BigDecimal.ZERO) > 0);
    }
}
