package com.example.myapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String symbol;

    private String name;

    @Column(precision = 12, scale = 2)
    private BigDecimal currentPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal previousClose;

    @Column(precision = 12, scale = 2)
    private BigDecimal dayChange;

    @Column(precision = 12, scale = 2)
    private BigDecimal dayChangePercent;

    private Long volume;

    @Column(precision = 15, scale = 2)
    private BigDecimal marketCap;

    private String sector;

    private String industry;

    private Boolean isActive;

    private LocalDateTime lastUpdated;
}
