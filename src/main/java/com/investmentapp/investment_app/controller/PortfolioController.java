package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.DTO.PortfolioDTO;
import com.investmentapp.investment_app.service.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@PreAuthorize("hasRole('USER')")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }
    // 1️⃣ Create a portfolio
    @PostMapping
    public ResponseEntity<PortfolioDTO> createPortfolio(@RequestBody PortfolioDTO portfolioDTO, @AuthenticationPrincipal UserDetails user) {
        PortfolioDTO createdPortfolio = portfolioService.createPortfolio(portfolioDTO, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPortfolio);
    }

    // 2️⃣ Get all portfolios for the logged-in user
    @GetMapping
    public ResponseEntity<List<PortfolioDTO>> getUserPortfolios(@AuthenticationPrincipal UserDetails user) {
        List<PortfolioDTO> portfolios = portfolioService.getUserPortfolios(user.getUsername());
        return ResponseEntity.ok(portfolios);
    }

    // 3️⃣ Get a specific portfolio
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDTO> getPortfolio(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        PortfolioDTO portfolio = portfolioService.getPortfolio(id, user.getUsername());
        return ResponseEntity.ok(portfolio);
    }

    // 4️⃣ Update portfolio details
    @PutMapping("/{id}")
    public ResponseEntity<PortfolioDTO> updatePortfolio(@PathVariable Long id, @RequestBody PortfolioDTO portfolioDTO, @AuthenticationPrincipal UserDetails user) {
        PortfolioDTO updatedPortfolio = portfolioService.updatePortfolio(id, portfolioDTO, user.getUsername());
        return ResponseEntity.ok(updatedPortfolio);
    }

    // 5️⃣ Delete a portfolio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        portfolioService.deletePortfolio(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
