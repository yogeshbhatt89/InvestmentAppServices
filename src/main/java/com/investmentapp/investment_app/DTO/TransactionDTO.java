package com.investmentapp.investment_app.DTO;

import com.investmentapp.investment_app.model.TransactionType;

import java.math.BigDecimal;

public class TransactionDTO {

    private Long portfolioId;  // Add portfolioId field
    private String stockSymbol;
    private TransactionType type;
    private int quantity;
    private BigDecimal price;
    private boolean isBuy;

    // Getters and setters

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
