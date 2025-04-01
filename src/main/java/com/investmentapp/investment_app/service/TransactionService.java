package com.investmentapp.investment_app.service;

import com.investmentapp.investment_app.DTO.TransactionDTO;
import com.investmentapp.investment_app.exception.NoHoldingsToSellException;
import com.investmentapp.investment_app.model.*;
import com.investmentapp.investment_app.repository.HoldingRepository;
import com.investmentapp.investment_app.repository.PortfolioRepository;
import com.investmentapp.investment_app.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TransactionService {

    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;
    private final HoldingRepository holdingRepository;

    public TransactionService(PortfolioRepository portfolioRepository,
                              TransactionRepository transactionRepository,
                              HoldingRepository holdingRepository) {
        this.portfolioRepository = portfolioRepository;
        this.transactionRepository = transactionRepository;
        this.holdingRepository = holdingRepository;
    }

    @Transactional
    public TransactionDTO executeTransaction(TransactionDTO dto, @AuthenticationPrincipal User user) {
        List<Portfolio> portfolios = portfolioRepository.findByUser(user);

        if (portfolios.isEmpty()) {
            throw new EntityNotFoundException("No portfolios found for the user");
        }

        Portfolio portfolio = portfolios.get(0);  // You may want to add logic for selecting a specific portfolio if the user has multiple portfolios.

        BigDecimal totalCost = dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));

        if (dto.getType() == TransactionType.BUY) {
            // Buying logic
            if (portfolio.getCurrentBalance().compareTo(totalCost) < 0) {
                throw new IllegalArgumentException("Insufficient balance");
            }
            portfolio.setCurrentBalance(portfolio.getCurrentBalance().subtract(totalCost));
            updateHoldings(portfolio, dto.getStockSymbol(), dto.getQuantity(), dto.getPrice(), true);
        } else if (dto.getType() == TransactionType.SELL) {
            // Selling logic
            Holding holding = holdingRepository.findByPortfolioIdAndStockSymbol(portfolio.getId(), dto.getStockSymbol())
                    .orElseThrow(() -> new NoHoldingsToSellException("No holdings to sell for stock: " + dto.getStockSymbol()));

            if (holding.getQuantity() < dto.getQuantity()) {
                throw new IllegalArgumentException("Not enough shares to sell");
            }

            holding.setQuantity(holding.getQuantity() - dto.getQuantity());
            portfolio.setCurrentBalance(portfolio.getCurrentBalance().add(totalCost));

            if (holding.getQuantity() == 0) {
                holdingRepository.delete(holding);
            } else {
                holdingRepository.save(holding);
            }
        }

        // Set transaction details
        Transaction transaction = new Transaction();
        transaction.setPortfolio(portfolio);
        transaction.setStockSymbol(dto.getStockSymbol());
        transaction.setQuantity(dto.getQuantity());
        transaction.setPrice(dto.getPrice());
        transaction.setTotalCost(totalCost);
        transaction.setType(dto.getType()); // Set the type directly from DTO
        transaction.setBuy(dto.getType() == TransactionType.BUY); // Set isBuy based on the type
        transactionRepository.save(transaction);
        portfolioRepository.save(portfolio);
        dto.setBuy(dto.getType() == TransactionType.BUY); // Ensure the 'buy' field in DTO is set correctly

        return dto;
    }




    private void updateHoldings(Portfolio portfolio, String stockSymbol, int quantity, BigDecimal price, boolean isBuy) {
        Holding holding = holdingRepository.findByPortfolioIdAndStockSymbol(portfolio.getId(), stockSymbol)
                .orElseGet(() -> createNewHolding(portfolio, stockSymbol));

        if (isBuy) {
            // Calculate new average price and total cost when buying
            BigDecimal totalValue = price.multiply(BigDecimal.valueOf(quantity));
            BigDecimal currentAveragePrice = holding.getAveragePrice() == null ? BigDecimal.ZERO : holding.getAveragePrice();
            BigDecimal newTotalValue = currentAveragePrice.multiply(BigDecimal.valueOf(holding.getQuantity()))
                    .add(totalValue);
            int newQuantity = holding.getQuantity() + quantity;
            BigDecimal newAveragePrice = newTotalValue.divide(BigDecimal.valueOf(newQuantity), RoundingMode.HALF_UP);

            holding.setAveragePrice(newAveragePrice);
            holding.setQuantity(newQuantity);
        } else {
            // Logic for selling (not shown in full for brevity)
            holding.setQuantity(holding.getQuantity() - quantity);
        }

        holdingRepository.save(holding);
    }

    private Holding createNewHolding(Portfolio portfolio, String stockSymbol) {
        Holding holding = new Holding();
        holding.setPortfolio(portfolio);
        holding.setStockSymbol(stockSymbol);
        holding.setQuantity(0);  // Initialize quantity
        holding.setAveragePrice(BigDecimal.ZERO);  // Initialize average price
        return holding;
    }

}
