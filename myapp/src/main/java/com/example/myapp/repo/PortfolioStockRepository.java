package com.example.myapp.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myapp.model.PortfolioStock;

public interface PortfolioStockRepository extends JpaRepository<PortfolioStock, Long> {

    List<PortfolioStock> findByPortfolioId(Long portfolioId);

    Optional<PortfolioStock> findByPortfolioIdAndStockId(Long portfolioId, Long stockId);
}
