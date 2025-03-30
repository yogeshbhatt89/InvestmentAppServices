package com.investmentapp.investment_app.DTO;

import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public class PortfolioDTO {

    private Long id;
    private String name;
    @Min(value = 0, message = "Initial balance must be non-negative")
    private Double initialBalance;

    @Min(value = 0, message = "Current balance must be non-negative")
    private Double currentBalance;
    private String riskTolerance;
    private LocalDateTime createdAt;

    // Constructor
    public PortfolioDTO(Long id, String name, Double initialBalance, Double currentBalance, String riskTolerance, LocalDateTime createdAt) {
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

    public Double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
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
