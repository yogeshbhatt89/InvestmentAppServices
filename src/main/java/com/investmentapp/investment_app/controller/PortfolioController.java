package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.dto.request.PortfolioRequest;
import com.investmentapp.investment_app.model.ApiResponse;
import com.investmentapp.investment_app.model.Holding;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.repository.HoldingRepository;
import com.investmentapp.investment_app.service.PortfolioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<?> getPortfolioHoldings(@PathVariable Long id) {
    List<Holding> holdings = holdingRepository.findByPortfolioId(id);
    return ResponseEntity.ok(
        ApiResponse.success(
            holdings, HttpStatus.OK.value(), "Holdings fetched", "portfolioId: " + id));
  }

  // 1️⃣ Create a portfolio
  @PostMapping
  public ResponseEntity<?> createPortfolio(
      @RequestBody PortfolioRequest portfolioRequest, @AuthenticationPrincipal User user) {
    PortfolioRequest createdPortfolio =
        portfolioService.createPortfolio(portfolioRequest, user.getEmail());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponse.success(
                createdPortfolio, HttpStatus.CREATED.value(), "Portfolio created", null));
  }

  // 2️⃣ Get all portfolios for the logged-in user
  @GetMapping
  public ResponseEntity<?> getUserPortfolios(@AuthenticationPrincipal User user) {
    List<PortfolioRequest> portfolios = portfolioService.getUserPortfolios(user.getEmail());
    return ResponseEntity.ok(
        ApiResponse.success(
            portfolios,
            HttpStatus.OK.value(),
            "Fetched user portfolios",
            "count: " + portfolios.size()));
  }

  // 3️⃣ Get a specific portfolio
  @GetMapping("/{id}")
  public ResponseEntity<?> getPortfolio(@PathVariable Long id, @AuthenticationPrincipal User user) {
    PortfolioRequest portfolio = portfolioService.getPortfolio(id, user.getEmail());
    return ResponseEntity.ok(
        ApiResponse.success(portfolio, HttpStatus.OK.value(), "Fetched portfolio", "id: " + id));
  }

  // 4️⃣ Update portfolio details
  @PutMapping("/{id}")
  public ResponseEntity<?> updatePortfolio(
      @PathVariable Long id,
      @Valid @RequestBody PortfolioRequest portfolioRequest,
      @AuthenticationPrincipal User user) {
    PortfolioRequest updatedPortfolio =
        portfolioService.updatePortfolio(id, portfolioRequest, user.getEmail());
    return ResponseEntity.ok(
        ApiResponse.success(
            updatedPortfolio, HttpStatus.OK.value(), "Portfolio updated", "id: " + id));
  }

  // 5️⃣ Delete a portfolio
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePortfolio(
      @PathVariable Long id, @AuthenticationPrincipal User user) {
    portfolioService.deletePortfolio(id, user.getEmail());
    Map<String, Object> data = new HashMap<>();
    data.put("deleted", true);
    data.put("id", id);
    return ResponseEntity.ok(
        ApiResponse.success(
            data, HttpStatus.OK.value(), "Portfolio deleted successfully", "id: " + id));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "INVALID_ARGUMENT", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
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
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILED", errors.toString()));
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            ApiResponse.error(HttpStatus.NOT_FOUND.value(), "RESOURCE_NOT_FOUND", ex.getMessage()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "ACCESS_DENIED", ex.getMessage()));
  }
}
