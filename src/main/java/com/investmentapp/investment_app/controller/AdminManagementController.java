package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.dto.request.PortfolioRequest;
import com.investmentapp.investment_app.dto.request.TransactionRequest;
import com.investmentapp.investment_app.mapper.PortfolioMapper;
import com.investmentapp.investment_app.model.ApiResponse;
import com.investmentapp.investment_app.model.GlobalSettings;
import com.investmentapp.investment_app.model.Portfolio;
import com.investmentapp.investment_app.model.Transaction;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.repository.GlobalSettingsRepository;
import com.investmentapp.investment_app.repository.InvestmentRepository;
import com.investmentapp.investment_app.repository.PortfolioRepository;
import com.investmentapp.investment_app.repository.TransactionRepository;
import com.investmentapp.investment_app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin")
public class AdminManagementController {

  private final UserRepository userRepository;
  private final PortfolioRepository portfolioRepository;
  private final TransactionRepository transactionRepository;
  private final InvestmentRepository investmentRepository;
  private final GlobalSettingsRepository globalSettingsRepository;

  public AdminManagementController(
      UserRepository userRepository,
      PortfolioRepository portfolioRepository,
      TransactionRepository transactionRepository,
      InvestmentRepository investmentRepository,
      GlobalSettingsRepository globalSettingsRepository) {
    this.userRepository = userRepository;
    this.portfolioRepository = portfolioRepository;
    this.transactionRepository = transactionRepository;
    this.investmentRepository = investmentRepository;
    this.globalSettingsRepository = globalSettingsRepository;
  }

  // #15 adminApis-getActiveUsers
  @GetMapping("/users/active")
  public ResponseEntity<?> getActiveUsers() {
    List<User> users = userRepository.findByIsActiveTrue();
    return ResponseEntity.ok(
        ApiResponse.success(
            users, HttpStatus.OK.value(), "Fetched active users", "count: " + users.size()));
  }

  // #6 adminApis-getUserById
  @GetMapping("/users/{id}")
  public ResponseEntity<?> getUserById(@PathVariable UUID id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(
              ApiResponse.error(
                  HttpStatus.NOT_FOUND.value(), "RESOURCE_NOT_FOUND", "User not found"));
    }
    return ResponseEntity.ok(
        ApiResponse.success(user.get(), HttpStatus.OK.value(), "Fetched user", "id: " + id));
  }

  // #7 adminApis-activateUserStatusById
  @PatchMapping("/users/{id}/activate")
  public ResponseEntity<?> activateUser(@PathVariable UUID id) {
    return setUserActiveStatus(id, true, "User activated");
  }

  // #8 adminApis-deactivateUserStatusById
  @PatchMapping("/users/{id}/deactivate")
  public ResponseEntity<?> deactivateUser(@PathVariable UUID id) {
    return setUserActiveStatus(id, false, "User deactivated");
  }

  private ResponseEntity<?> setUserActiveStatus(UUID id, boolean active, String message) {
    Optional<User> userOpt = userRepository.findById(id);
    if (userOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(
              ApiResponse.error(
                  HttpStatus.NOT_FOUND.value(), "RESOURCE_NOT_FOUND", "User not found"));
    }
    User user = userOpt.get();
    user.setIsActive(active);
    userRepository.save(user);
    return ResponseEntity.ok(
        ApiResponse.success(
            Map.of("id", id.toString(), "isActive", active),
            HttpStatus.OK.value(),
            message,
            null));
  }

  // #9 adminApis-getAllUserPortfolios
  @GetMapping("/users/{id}/portfolios")
  public ResponseEntity<?> getAllUserPortfolios(@PathVariable UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    List<Portfolio> portfolios = portfolioRepository.findByUser(user);
    List<PortfolioRequest> dtos =
        portfolios.stream().map(PortfolioMapper::toDTO).collect(Collectors.toList());
    return ResponseEntity.ok(
        ApiResponse.success(
            dtos, HttpStatus.OK.value(), "Fetched user portfolios", "count: " + dtos.size()));
  }

  // #11 adminApis-deletePorfoliosById
  // Usage: DELETE /api/admin/portfolios?ids=1,2,3
  @DeleteMapping("/portfolios")
  public ResponseEntity<?> deletePortfoliosByIds(@RequestParam(name = "ids") String idsCsv) {
    List<Long> ids =
        Arrays.stream(idsCsv.split(",")).map(String::trim).filter(s -> !s.isEmpty())
            .map(Long::valueOf)
            .collect(Collectors.toList());
    List<Long> deleted = new ArrayList<>();
    List<Long> notFound = new ArrayList<>();
    ids.forEach(
        id -> {
          if (portfolioRepository.existsById(id)) {
            portfolioRepository.deleteById(id);
            deleted.add(id);
          } else {
            notFound.add(id);
          }
        });
    return ResponseEntity.ok(
        ApiResponse.success(
            Map.of("deleted", deleted, "notFound", notFound),
            HttpStatus.OK.value(),
            "Processed portfolio deletions",
            null));
  }

  // #10 adminApis-getAllTransactions
  @GetMapping("/transactions")
  public ResponseEntity<?> getAllTransactions() {
    List<Transaction> transactions = transactionRepository.findAll();
    List<TransactionRequest> dtos =
        transactions.stream()
            .map(
                t ->
                    new TransactionRequest(
                        t.getPortfolio().getId(),
                        t.getStockSymbol(),
                        t.getType(),
                        t.getQuantity(),
                        t.getPrice(),
                        t.getTransactionDate()))
            .collect(Collectors.toList());
    return ResponseEntity.ok(
        ApiResponse.success(
            dtos, HttpStatus.OK.value(), "Fetched all transactions", "count: " + dtos.size()));
  }

  // #16 adminApis-getTotalInvestments (returns total count of investments)
  @GetMapping("/investments/total")
  public ResponseEntity<?> getTotalInvestments() {
    long total = investmentRepository.count();
    return ResponseEntity.ok(
        ApiResponse.success(
            Map.of("totalInvestments", total),
            HttpStatus.OK.value(),
            "Fetched total investments count",
            null));
  }

  // #14 adminApis-updateMainenanceMode
  @PatchMapping("/settings/maintenance")
  public ResponseEntity<?> updateMaintenanceMode(@RequestBody Map<String, Object> body) {
    boolean enabled = Boolean.TRUE.equals(body.get("enabled"));
    GlobalSettings settings = getOrCreateSettings();
    settings.setMaintenanceEnabled(enabled);
    globalSettingsRepository.save(settings);
    return ResponseEntity.ok(
        ApiResponse.success(
            Map.of("maintenanceEnabled", enabled),
            HttpStatus.OK.value(),
            "Maintenance mode updated",
            null));
  }

  // #13 adminApis-updateGlobalAlert
  @PatchMapping("/settings/alert")
  public ResponseEntity<?> updateGlobalAlert(@RequestBody Map<String, Object> body) {
    boolean enabled = body.get("enabled") != null && (Boolean) body.get("enabled");
    String message = (String) body.getOrDefault("message", null);
    String severity = (String) body.getOrDefault("severity", null);

    GlobalSettings settings = getOrCreateSettings();
    settings.setGlobalAlertEnabled(enabled);
    settings.setGlobalAlertMessage(message);
    settings.setGlobalAlertSeverity(severity);
    globalSettingsRepository.save(settings);

    return ResponseEntity.ok(
        ApiResponse.success(
            Map.of(
                "globalAlertEnabled", enabled,
                "globalAlertMessage", message,
                "globalAlertSeverity", severity),
            HttpStatus.OK.value(),
            "Global alert updated",
            null));
  }

  // #12 adminApis-updateGlobalSettings (patch arbitrary settings fields)
  @PatchMapping("/settings")
  public ResponseEntity<?> updateGlobalSettings(@RequestBody Map<String, Object> body) {
    GlobalSettings settings = getOrCreateSettings();
    if (body.containsKey("maintenanceEnabled")) {
      settings.setMaintenanceEnabled(Boolean.TRUE.equals(body.get("maintenanceEnabled")));
    }
    if (body.containsKey("globalAlertEnabled")) {
      settings.setGlobalAlertEnabled(Boolean.TRUE.equals(body.get("globalAlertEnabled")));
    }
    if (body.containsKey("globalAlertMessage")) {
      settings.setGlobalAlertMessage((String) body.get("globalAlertMessage"));
    }
    if (body.containsKey("globalAlertSeverity")) {
      settings.setGlobalAlertSeverity((String) body.get("globalAlertSeverity"));
    }
    globalSettingsRepository.save(settings);

    return ResponseEntity.ok(
        ApiResponse.success(settings, HttpStatus.OK.value(), "Global settings updated", null));
  }

  private GlobalSettings getOrCreateSettings() {
    return globalSettingsRepository.findAll().stream()
        .findFirst()
        .orElseGet(() -> globalSettingsRepository.save(new GlobalSettings()));
  }
}
