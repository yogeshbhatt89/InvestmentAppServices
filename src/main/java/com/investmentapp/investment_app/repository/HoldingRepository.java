package com.investmentapp.investment_app.repository;

import com.investmentapp.investment_app.model.Holding;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
  List<Holding> findByPortfolioId(Long portfolioId);

  Optional<Holding> findByPortfolioIdAndStockSymbol(Long portfolioId, String stockSymbol);
}
