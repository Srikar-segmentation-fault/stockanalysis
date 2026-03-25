package com.example.myapp.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myapp.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findBySymbol(String symbol);
    
    List<Stock> findBySymbolContainingIgnoreCase(String symbol);
    
    List<Stock> findByNameContainingIgnoreCase(String name);
}
