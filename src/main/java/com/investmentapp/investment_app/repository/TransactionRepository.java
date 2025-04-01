package com.investmentapp.investment_app.repository;

import com.investmentapp.investment_app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByPortfolioId(Long portfolioId);
}
