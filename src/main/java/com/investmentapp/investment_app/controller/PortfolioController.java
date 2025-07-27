package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.dto.request.PortfolioRequest;
import com.investmentapp.investment_app.model.Holding;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.repository.HoldingRepository;
import com.investmentapp.investment_app.service.PortfolioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portfolios")
@PreAuthorize("hasRole('ROLE_USER')")
public class PortfolioController {

  private final PortfolioService portfolioService;
  private final HoldingRepository holdingRepository;

  // Refactor constructor to accept both dependencies
  public PortfolioController(
      PortfolioService portfolioService, HoldingRepository holdingRepository) {
    this.portfolioService = portfolioService;
    this.holdingRepository = holdingRepository;
  }

  @GetMapping("/{id}/holdings")
  public ResponseEntity<List<Holding>> getPortfolioHoldings(@PathVariable Long id) {
    return ResponseEntity.ok(holdingRepository.findByPortfolioId(id));
  }

  // 1️⃣ Create a portfolio
  @PostMapping
  public ResponseEntity<PortfolioRequest> createPortfolio(
          @RequestBody PortfolioRequest portfolioRequest, @AuthenticationPrincipal User user) {
    PortfolioRequest createdPortfolio = portfolioService.createPortfolio(portfolioRequest, user.getEmail());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPortfolio);
  }

  // 2️⃣ Get all portfolios for the logged-in user
  @GetMapping
  public ResponseEntity<List<PortfolioRequest>> getUserPortfolios(@AuthenticationPrincipal User user) {
    List<PortfolioRequest> portfolios = portfolioService.getUserPortfolios(user.getEmail());
    return ResponseEntity.ok(portfolios);
  }

  // 3️⃣ Get a specific portfolio
  @GetMapping("/{id}")
  public ResponseEntity<PortfolioRequest> getPortfolio(
      @PathVariable Long id, @AuthenticationPrincipal User user) {
    PortfolioRequest portfolio = portfolioService.getPortfolio(id, user.getEmail());
    return ResponseEntity.ok(portfolio);
  }

  // 4️⃣ Update portfolio details
  @PutMapping("/{id}")
  public ResponseEntity<PortfolioRequest> updatePortfolio(
      @PathVariable Long id,
      @Valid @RequestBody PortfolioRequest portfolioRequest,
      @AuthenticationPrincipal User user) {
    PortfolioRequest updatedPortfolio =
        portfolioService.updatePortfolio(id, portfolioRequest, user.getEmail());
    return ResponseEntity.ok(updatedPortfolio);
  }

  // 5️⃣ Delete a portfolio
  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, String>> deletePortfolio(
      @PathVariable Long id, @AuthenticationPrincipal User user) {
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
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    System.err.println("handleValidationExceptions called");
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
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
