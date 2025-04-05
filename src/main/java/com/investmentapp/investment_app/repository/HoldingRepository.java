package com.investmentapp.investment_app.repository;

import com.investmentapp.investment_app.model.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findByPortfolioId(Long portfolioId);
    Optional<Holding> findByPortfolioIdAndStockSymbol(Long portfolioId, String stockSymbol);
}
