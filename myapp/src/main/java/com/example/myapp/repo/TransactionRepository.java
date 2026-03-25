package com.example.myapp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myapp.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByPortfolioId(Long portfolioId);
}
