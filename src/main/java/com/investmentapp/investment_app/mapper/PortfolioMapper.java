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
                portfolio.getCreatedAt()
        );
    }
}
