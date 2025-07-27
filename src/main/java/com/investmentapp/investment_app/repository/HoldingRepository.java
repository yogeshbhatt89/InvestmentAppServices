package com.investmentapp.investment_app.repository;

import com.investmentapp.investment_app.model.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {
  List<Holding> findByPortfolioId(Long portfolioId);

  Optional<Holding> findByPortfolioIdAndStockSymbol(Long portfolioId, String stockSymbol);
}
