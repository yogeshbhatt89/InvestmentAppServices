package com.investmentapp.investment_app.repository;

import com.investmentapp.investment_app.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  /**
   * Find a user by their username.
   *
   * @param username the username to search
   * @return an optional containing the user if found
   */
  Optional<User> findByUsername(String username);

  /**
   * Find a user by their email address.
   *
   * @param email the email to search
   * @return an optional containing the user if found
   */
  Optional<User> findByEmail(String email);

  /**
   * Check if a user with the given email already exists.
   *
   * @param email the email to check
   * @return true if exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Check if a user with the given username already exists.
   *
   * @param username the username to check
   * @return true if exists, false otherwise
   */
  boolean existsByUsername(String username);

  /** Find all active users. */
  List<User> findByIsActiveTrue();
}
