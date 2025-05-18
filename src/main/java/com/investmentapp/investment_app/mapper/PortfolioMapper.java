package com.investmentapp.investment_app.mapper;

import com.investmentapp.investment_app.DTO.PortfolioDTO;
import com.investmentapp.investment_app.model.Portfolio;

public class PortfolioMapper {

  public static PortfolioDTO toDTO(Portfolio portfolio) {
    return new PortfolioDTO(
        portfolio.getId(),
        portfolio.getName(),
        portfolio.getInitialBalance(),
        portfolio.getCurrentBalance(),
        portfolio.getRiskTolerance(),
        portfolio.getCreatedAt());
  }

  public static Portfolio toEntity(PortfolioDTO dto) {
    Portfolio portfolio = new Portfolio();
    portfolio.setId(dto.getId());
    portfolio.setName(dto.getName());
    portfolio.setInitialBalance(dto.getInitialBalance()); // Ensure BigDecimal
    portfolio.setCurrentBalance(dto.getCurrentBalance()); // Ensure BigDecimal
    portfolio.setRiskTolerance(dto.getRiskTolerance());
    portfolio.setCreatedAt(dto.getCreatedAt());
    return portfolio;
  }
}
