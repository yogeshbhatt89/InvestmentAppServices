package com.investmentapp.investment_app.repository;

import com.investmentapp.investment_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {  // Use UUID as the primary key type
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
