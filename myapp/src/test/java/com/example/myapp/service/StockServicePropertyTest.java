package com.example.myapp.service;

import com.example.myapp.model.Stock;
import com.example.myapp.repo.StockRepository;
import net.jqwik.api.*;
import net.jqwik.api.constraints.NotBlank;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Property-based tests for StockService using jqwik.
 * Feature: ai-stock-analysis
 */
class StockServicePropertyTest {

    @Mock
    private StockRepository stockRepository;

    private StockService stockService;

    @BeforeTry
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stockService = new StockService(stockRepository);
    }

    /**
     * Feature: ai-stock-analysis, Property 9: Symbol validation before API calls
     * Validates: Requirements 2.4
     * 
     * This property verifies that invalid stock symbols are rejected before making
     * external API calls. The StockService should validate symbol format and throw
     * IllegalArgumentException for invalid symbols without invoking the repository.
     */
    @Property(tries = 100)
    void invalidSymbolsShouldBeRejectedBeforeApiCalls(
            @ForAll("invalidStockSymbols") String invalidSymbol) {
        
        // When: attempting to get stock details with an invalid symbol
        // Then: should throw IllegalArgumentException
        assertThatThrownBy(() -> stockService.getStockDetails(invalidSymbol))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("symbol");
        
        // And: repository should NOT be called (validation happens before API call)
        verify(stockRepository, never()).findBySymbol(anyString());
    }

    /**
     * Provides various invalid stock symbol formats for property-based testing.
     * Invalid symbols include:
     * - null values
     * - empty strings
     * - whitespace-only strings
     * - symbols with special characters
     * - symbols that are too long (>5 characters)
     * - symbols with lowercase letters
     * - symbols with numbers
     * - symbols with mixed case
     */
    @Provide
    Arbitrary<String> invalidStockSymbols() {
        return Arbitraries.oneOf(
                // Null and empty cases
                Arbitraries.just(null),
                Arbitraries.just(""),
                Arbitraries.just("   "),
                Arbitraries.just("\t"),
                Arbitraries.just("\n"),
                
                // Too long (>5 characters)
                Arbitraries.strings()
                        .withCharRange('A', 'Z')
                        .ofMinLength(6)
                        .ofMaxLength(10),
                
                // Contains special characters
                Arbitraries.strings()
                        .withCharRange('!', '/')
                        .ofMinLength(1)
                        .ofMaxLength(5),
                
                // Contains numbers
                Arbitraries.strings()
                        .withCharRange('0', '9')
                        .ofMinLength(1)
                        .ofMaxLength(5),
                
                // Contains lowercase letters
                Arbitraries.strings()
                        .withCharRange('a', 'z')
                        .ofMinLength(1)
                        .ofMaxLength(5),
                
                // Mixed case
                Arbitraries.strings()
                        .alpha()
                        .ofMinLength(1)
                        .ofMaxLength(5)
                        .filter(s -> !s.equals(s.toUpperCase())),
                
                // Contains spaces
                Arbitraries.strings()
                        .withCharRange('A', 'Z')
                        .ofMinLength(1)
                        .ofMaxLength(3)
                        .map(s -> s + " " + s),
                
                // Contains hyphens or dots
                Arbitraries.of("A-B", "A.B", "AB-C", "A..B", "-ABC", "ABC-")
        );
    }

    /**
     * Additional property: Valid symbols should pass validation and attempt repository lookup.
     * This serves as a sanity check that valid symbols are not incorrectly rejected.
     */
    @Property(tries = 100)
    void validSymbolsShouldPassValidation(
            @ForAll("validStockSymbols") String validSymbol) {
        
        // Given: repository returns empty (stock not found)
        when(stockRepository.findBySymbol(anyString())).thenReturn(Optional.empty());
        
        // When: attempting to get stock details with a valid symbol
        // Then: should pass validation and call repository (even if stock not found)
        try {
            stockService.getStockDetails(validSymbol);
        } catch (RuntimeException e) {
            // Expected: "Stock not found" exception after validation passes
            assertThat(e.getMessage()).contains("Stock not found");
        }
        
        // And: repository should be called (validation passed)
        verify(stockRepository, atLeastOnce()).findBySymbol(anyString());
        
        // Reset mock for next iteration
        reset(stockRepository);
    }

    /**
     * Provides valid stock symbol formats for property-based testing.
     * Valid symbols are 1-5 uppercase letters.
     */
    @Provide
    Arbitrary<String> validStockSymbols() {
        return Arbitraries.strings()
                .withCharRange('A', 'Z')
                .ofMinLength(1)
                .ofMaxLength(5);
    }
}
