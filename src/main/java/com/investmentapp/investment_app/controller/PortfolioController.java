package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.DTO.PortfolioDTO;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.service.PortfolioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portfolios")
@PreAuthorize("hasRole('ROLE_USER')")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // 1️⃣ Create a portfolio
    @PostMapping
    public ResponseEntity<PortfolioDTO> createPortfolio(@RequestBody PortfolioDTO portfolioDTO, @AuthenticationPrincipal User user) {
        PortfolioDTO createdPortfolio = portfolioService.createPortfolio(portfolioDTO, user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPortfolio);
    }

    // 2️⃣ Get all portfolios for the logged-in user
    @GetMapping
    public ResponseEntity<List<PortfolioDTO>> getUserPortfolios(@AuthenticationPrincipal User user) {
        List<PortfolioDTO> portfolios = portfolioService.getUserPortfolios(user.getEmail());
        return ResponseEntity.ok(portfolios);
    }

    // 3️⃣ Get a specific portfolio
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDTO> getPortfolio(@PathVariable Long id, @AuthenticationPrincipal User user) {
        PortfolioDTO portfolio = portfolioService.getPortfolio(id, user.getEmail());
        return ResponseEntity.ok(portfolio);
    }

    // 4️⃣ Update portfolio details
    @PutMapping("/{id}")
    public ResponseEntity<PortfolioDTO> updatePortfolio(@PathVariable Long id, @Valid @RequestBody PortfolioDTO portfolioDTO, @AuthenticationPrincipal User user) {
        PortfolioDTO updatedPortfolio = portfolioService.updatePortfolio(id, portfolioDTO, user.getEmail());
        return ResponseEntity.ok(updatedPortfolio);
    }

    // 5️⃣ Delete a portfolio
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePortfolio(@PathVariable Long id, @AuthenticationPrincipal User user) {
        portfolioService.deletePortfolio(id, user.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Portfolio deleted successfully");
        return ResponseEntity.ok(response);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        System.err.println("handleValidationExceptions called");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            System.err.println("Error: " + fieldName + " - " + errorMessage);
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}