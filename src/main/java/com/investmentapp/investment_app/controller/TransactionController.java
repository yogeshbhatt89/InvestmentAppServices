package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.dto.request.TransactionRequest;
import com.investmentapp.investment_app.model.ApiResponse;
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
  public ResponseEntity<?> createTransaction(
      @RequestBody TransactionRequest transactionRequest, @AuthenticationPrincipal User user) {
    try {
      TransactionRequest result = transactionService.executeTransaction(transactionRequest, user);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(
              ApiResponse.success(result, HttpStatus.CREATED.value(), "Transaction created", null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(
              ApiResponse.error(
                  HttpStatus.BAD_REQUEST.value(), "TRANSACTION_FAILED", e.getMessage()));
    }
  }

  @GetMapping("/history")
  public ResponseEntity<?> getTransactionHistory(@AuthenticationPrincipal User user) {
    List<TransactionRequest> history = transactionService.getTransactionHistory(user);
    return ResponseEntity.ok(
        ApiResponse.success(history, HttpStatus.OK.value(), "Transaction history fetched", null));
  }

  @GetMapping("/history/{portfolioId}")
  public ResponseEntity<?> getTransactionHistoryByPortfolioId(
      @PathVariable Long portfolioId, @AuthenticationPrincipal User user) {
    List<TransactionRequest> history =
        transactionService.getTransactionHistoryByPortfolioId(portfolioId);
    return ResponseEntity.ok(
        ApiResponse.success(
            history,
            HttpStatus.OK.value(),
            "Transaction history fetched",
            "portfolioId: " + portfolioId));
  }
}
