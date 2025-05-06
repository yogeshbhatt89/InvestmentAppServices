package com.investmentapp.investment_app.repository;

import com.investmentapp.investment_app.model.Portfolio;
import com.investmentapp.investment_app.model.User; // Import the correct User entity
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
  List<Portfolio> findByUser(User user);
}
