package com.investmentapp.investment_app.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PortfolioDTO {

    private Long id;
    private String name;
    @Min(value = 0, message = "Initial balance must be non-negative")
    private BigDecimal initialBalance;  // Change to BigDecimal

    private BigDecimal currentBalance;  // Change to BigDecimal
    private String riskTolerance;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Constructor
    public PortfolioDTO() {
    }


    public PortfolioDTO(Long id, String name, BigDecimal initialBalance, BigDecimal currentBalance, String riskTolerance, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.initialBalance = initialBalance;
        this.currentBalance = currentBalance;
        this.riskTolerance = riskTolerance;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getRiskTolerance() {
        return riskTolerance;
    }

    public void setRiskTolerance(String riskTolerance) {
        this.riskTolerance = riskTolerance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
