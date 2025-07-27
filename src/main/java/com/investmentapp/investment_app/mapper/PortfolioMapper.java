package com.investmentapp.investment_app.mapper;

import com.investmentapp.investment_app.dto.request.PortfolioRequest;
import com.investmentapp.investment_app.model.Portfolio;

public class PortfolioMapper {

  public static PortfolioRequest toDTO(Portfolio portfolio) {
    return new PortfolioRequest(
        portfolio.getId(),
        portfolio.getName(),
        portfolio.getInitialBalance(),
        portfolio.getCurrentBalance(),
        portfolio.getRiskTolerance(),
        portfolio.getCreatedAt());
  }

  public static Portfolio toEntity(PortfolioRequest dto) {
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
