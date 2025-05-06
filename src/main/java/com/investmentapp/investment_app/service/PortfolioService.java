package com.investmentapp.investment_app.service;

import com.investmentapp.investment_app.DTO.PortfolioDTO;
import com.investmentapp.investment_app.mapper.PortfolioMapper;
import com.investmentapp.investment_app.model.Portfolio;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.repository.PortfolioRepository;
import com.investmentapp.investment_app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class PortfolioService {

  private final PortfolioRepository portfolioRepository;
  private final UserRepository userRepository;

  public PortfolioService(PortfolioRepository portfolioRepository, UserRepository userRepository) {
    this.portfolioRepository = portfolioRepository;
    this.userRepository = userRepository;
  }

  // 1️⃣ Create Portfolio
  public PortfolioDTO createPortfolio(@Valid PortfolioDTO dto, String email) {
    System.out.println(
        "PortfolioService: createPortfolio called for email: " + email); // Added logging
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    System.out.println(
        "PortfolioService: createPortfolio found user: " + user.getUsername()); // Added logging

    Portfolio portfolio = new Portfolio();
    portfolio.setUser(user);
    portfolio.setName(dto.getName());
    portfolio.setInitialBalance(dto.getInitialBalance()); // Make sure this is BigDecimal
    portfolio.setCurrentBalance(dto.getInitialBalance()); // Make sure this is BigDecimal
    portfolio.setRiskTolerance(dto.getRiskTolerance());

    Portfolio savedPortfolio = portfolioRepository.save(portfolio);
    return PortfolioMapper.toDTO(savedPortfolio);
  }

  // 2️⃣ Get User's Portfolios
  public List<PortfolioDTO> getUserPortfolios(String email) {
    User user =
        userRepository
            .findByEmail(email) // Use User instead of AppUser
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return portfolioRepository.findByUser(user).stream()
        .map(PortfolioMapper::toDTO)
        .collect(Collectors.toList());
  }

  // 3️⃣ Get Portfolio by ID
  public PortfolioDTO getPortfolio(Long id, String email) {
    Portfolio portfolio =
        portfolioRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));

    if (!portfolio.getUser().getEmail().equals(email)) {
      System.err.println("PortfolioService: AccessDeniedException: User does not own portfolio");
      throw new AccessDeniedException("You do not own this portfolio");
    }

    return PortfolioMapper.toDTO(portfolio);
  }

  // 4️⃣ Update Portfolio
  public PortfolioDTO updatePortfolio(Long id, @Valid PortfolioDTO dto, String email) {
    System.out.println(
        "PortfolioService: updatePortfolio called for id: " + id + ", email: " + email);
    Portfolio portfolio =
        portfolioRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));

    System.out.println(
        "PortfolioService: portfolio owner email: " + portfolio.getUser().getEmail());

    if (!portfolio.getUser().getEmail().equals(email)) {
      System.err.println("PortfolioService: AccessDeniedException: User does not own portfolio");
      throw new AccessDeniedException("You do not own this portfolio");
    }

    if (dto.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Initial balance must be non-negative");
    }

    if (dto.getCurrentBalance().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Current balance must be non-negative");
    }

    portfolio.setName(dto.getName());
    portfolio.setRiskTolerance(dto.getRiskTolerance());
    portfolio.setInitialBalance(dto.getInitialBalance());
    portfolio.setCurrentBalance(dto.getCurrentBalance());
    portfolioRepository.save(portfolio);

    return PortfolioMapper.toDTO(portfolio);
  }

  // 5️⃣ Delete Portfolio
  public void deletePortfolio(Long id, String email) {
    Portfolio portfolio =
        portfolioRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));

    if (!portfolio.getUser().getEmail().equals(email)) {
      throw new AccessDeniedException("You do not own this portfolio");
    }

    portfolioRepository.delete(portfolio);
  }
}
