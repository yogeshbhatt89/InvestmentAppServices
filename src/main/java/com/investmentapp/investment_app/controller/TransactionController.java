package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.dto.request.TransactionRequest;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.service.TransactionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping
  public ResponseEntity<TransactionRequest> createTransaction(
      @RequestBody TransactionRequest transactionRequest, @AuthenticationPrincipal User user) {
    try {
      TransactionRequest result = transactionService.executeTransaction(transactionRequest, user);
      return new ResponseEntity<>(result, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/history")
  public ResponseEntity<List<TransactionRequest>> getTransactionHistory(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(transactionService.getTransactionHistory(user));
  }

  @GetMapping("/history/{portfolioId}")
  public List<TransactionRequest> getTransactionHistoryByPortfolioId(
      @PathVariable Long portfolioId, @AuthenticationPrincipal User user) {
    return transactionService.getTransactionHistoryByPortfolioId(portfolioId);
  }
}
