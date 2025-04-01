package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.DTO.TransactionDTO;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> executeTransaction(@RequestBody TransactionDTO transactionDTO, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transactionService.executeTransaction(transactionDTO, user));
    }

}
