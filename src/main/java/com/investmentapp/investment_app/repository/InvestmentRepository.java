package com.investmentapp.investment_app.repository;

import com.investmentapp.investment_app.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    Optional<Investment> findBySymbol(String symbol);
}
