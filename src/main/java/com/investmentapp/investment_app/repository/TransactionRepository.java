package com.investmentapp.investment_app.repository;

import com.investmentapp.investment_app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  List<Transaction> findByPortfolioIdIn(List<Long> portfolioIds);
}
