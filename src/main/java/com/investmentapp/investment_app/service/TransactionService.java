package com.investmentapp.investment_app.service;

import com.investmentapp.investment_app.dto.request.TransactionRequest;
import com.investmentapp.investment_app.enums.TransactionType;
import com.investmentapp.investment_app.exception.AccessDeniedException;
import com.investmentapp.investment_app.exception.InsufficientBalanceException;
import com.investmentapp.investment_app.exception.NoHoldingsToSellException;
import com.investmentapp.investment_app.model.*;
import com.investmentapp.investment_app.repository.HoldingRepository;
import com.investmentapp.investment_app.repository.InvestmentRepository;
import com.investmentapp.investment_app.repository.PortfolioRepository;
import com.investmentapp.investment_app.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

  private final PortfolioRepository portfolioRepository;
  private final TransactionRepository transactionRepository;
  private final HoldingRepository holdingRepository;
  private final InvestmentRepository investmentRepository;

  public TransactionService(
      PortfolioRepository portfolioRepository,
      TransactionRepository transactionRepository,
      HoldingRepository holdingRepository,
      InvestmentRepository investmentRepository) {
    this.portfolioRepository = portfolioRepository;
    this.transactionRepository = transactionRepository;
    this.holdingRepository = holdingRepository;
    this.investmentRepository = investmentRepository;
  }

  @Transactional
  public TransactionRequest executeTransaction(
      TransactionRequest dto, @AuthenticationPrincipal User user) {
    // Fetch the portfolio directly using the ID from the request
    Portfolio portfolio =
        portfolioRepository
            .findById(dto.getPortfolioId())
            .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));

    // Ensure the portfolio belongs to the authenticated user
    if (!portfolio.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("You do not have access to this portfolio");
    }

    BigDecimal totalCost = dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));

    if (dto.getType() == TransactionType.BUY) {
      if (portfolio.getCurrentBalance().compareTo(totalCost) < 0) {
        throw new InsufficientBalanceException("Insufficient balance.");
      }
      portfolio.setCurrentBalance(portfolio.getCurrentBalance().subtract(totalCost));
      updateHoldings(portfolio, dto.getStockSymbol(), dto.getQuantity(), dto.getPrice(), true);
    } else if (dto.getType() == TransactionType.SELL) {
      Holding holding =
          holdingRepository
              .findByPortfolioIdAndStockSymbol(portfolio.getId(), dto.getStockSymbol())
              .orElseThrow(
                  () ->
                      new NoHoldingsToSellException(
                          "No holdings to sell for stock: " + dto.getStockSymbol()));

      if (holding.getQuantity() < dto.getQuantity()) {
        throw new IllegalArgumentException("Not enough shares to sell.");
      }

      holding.setQuantity(holding.getQuantity() - dto.getQuantity());
      portfolio.setCurrentBalance(portfolio.getCurrentBalance().add(totalCost));

      if (holding.getQuantity() == 0) {
        holdingRepository.delete(holding);
      } else {
        holdingRepository.save(holding);
      }
    }

    Investment investment =
        investmentRepository
            .findBySymbol(dto.getStockSymbol())
            .orElseGet(
                () -> {
                  Investment newInvestment = new Investment();
                  newInvestment.setSymbol(dto.getStockSymbol());
                  return investmentRepository.save(newInvestment);
                });

    Transaction transaction = new Transaction();
    transaction.setPortfolio(portfolio);
    transaction.setInvestment(investment);
    transaction.setStockSymbol(dto.getStockSymbol());
    transaction.setQuantity(dto.getQuantity());
    transaction.setPrice(dto.getPrice());
    transaction.setTotalCost(totalCost);
    transaction.setType(dto.getType());
    transaction.setTransactionDate(LocalDateTime.now());

    transactionRepository.save(transaction);
    portfolioRepository.save(portfolio);

    return dto;
  }

  private void updateHoldings(
      Portfolio portfolio, String stockSymbol, int quantity, BigDecimal price, boolean isBuy) {
    Holding holding =
        holdingRepository
            .findByPortfolioIdAndStockSymbol(portfolio.getId(), stockSymbol)
            .orElseGet(() -> createNewHolding(portfolio, stockSymbol));

    if (isBuy) {
      // Update holding when buying
      BigDecimal totalValue = price.multiply(BigDecimal.valueOf(quantity));
      BigDecimal currentAveragePrice =
          holding.getAveragePrice() == null ? BigDecimal.ZERO : holding.getAveragePrice();
      BigDecimal newTotalValue =
          currentAveragePrice.multiply(BigDecimal.valueOf(holding.getQuantity())).add(totalValue);
      int newQuantity = holding.getQuantity() + quantity;
      BigDecimal newAveragePrice =
          newTotalValue.divide(BigDecimal.valueOf(newQuantity), RoundingMode.HALF_UP);

      holding.setAveragePrice(newAveragePrice);
      holding.setQuantity(newQuantity);
    } else {
      // Selling logic (not fully implemented in your code, should handle average price
      // recalculation if necessary)
      holding.setQuantity(holding.getQuantity() - quantity);
    }

    holdingRepository.save(holding);
  }

  public List<TransactionRequest> getTransactionHistory(User user) {
    List<Portfolio> portfolios = portfolioRepository.findByUser(user);

    if (portfolios.isEmpty()) {
      throw new EntityNotFoundException("No portfolios found for the user");
    }

    List<Long> portfolioIds =
        portfolios.stream().map(Portfolio::getId).collect(Collectors.toList());

    List<Transaction> transactions = transactionRepository.findByPortfolioIdIn(portfolioIds);

    return transactions.stream()
        .map(
            transaction ->
                new TransactionRequest(
                    transaction.getPortfolio().getId(),
                    transaction.getStockSymbol(),
                    transaction.getType(),
                    transaction.getQuantity(),
                    transaction.getPrice(),
                    transaction.getTransactionDate()))
        .collect(Collectors.toList());
  }

  public List<TransactionRequest> getTransactionHistoryByPortfolioId(Long portfolioId) {
    List<Transaction> transactions =
        transactionRepository.findByPortfolioIdIn(List.of(portfolioId));

    if (transactions.isEmpty()) {
      throw new EntityNotFoundException(
          "No transactions found for the portfolio with ID: " + portfolioId);
    }

    return transactions.stream()
        .map(
            transaction ->
                new TransactionRequest(
                    transaction.getPortfolio().getId(),
                    transaction.getStockSymbol(),
                    transaction.getType(),
                    transaction.getQuantity(),
                    transaction.getPrice(),
                    transaction.getTransactionDate()))
        .collect(Collectors.toList());
  }

  private Holding createNewHolding(Portfolio portfolio, String stockSymbol) {
    Holding holding = new Holding();
    holding.setPortfolio(portfolio);
    holding.setStockSymbol(stockSymbol);
    holding.setQuantity(0); // Initialize quantity
    holding.setAveragePrice(BigDecimal.ZERO); // Initialize average price
    return holding;
  }
}
