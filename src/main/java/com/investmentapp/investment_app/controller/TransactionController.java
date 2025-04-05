package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.DTO.TransactionDTO;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO, @AuthenticationPrincipal User user) {
        try {
            TransactionDTO result = transactionService.executeTransaction(transactionDTO, user);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionDTO>> getTransactionHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(user));
    }

    @GetMapping("/history/{portfolioId}")
    public List<TransactionDTO> getTransactionHistoryByPortfolioId(@PathVariable Long portfolioId,@AuthenticationPrincipal User user ) {
        return transactionService.getTransactionHistoryByPortfolioId(portfolioId);
    }
}
