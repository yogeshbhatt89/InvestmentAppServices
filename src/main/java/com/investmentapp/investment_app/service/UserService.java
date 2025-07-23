package com.investmentapp.investment_app.service;

import com.investmentapp.investment_app.DTO.UserDTO;
import com.investmentapp.investment_app.exception.EmailAlreadyExistsException;
import com.investmentapp.investment_app.exception.UserNotFoundException;
import com.investmentapp.investment_app.model.Role;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  public User registerUser(
      String fullName, String email, String password, String username, Set<Role> roles) {
    // Check if the email already exists
    if (userRepository.findByEmail(email).isPresent()) {
      throw new EmailAlreadyExistsException("Email already exists!");
    }

    String passwordHash = passwordEncoder.encode(password);

    User user = new User();
    user.setFullName(fullName);
    user.setEmail(email);
    user.setPasswordHash(passwordHash);
    user.setUsername(username);
    user.setCreatedAt(LocalDateTime.now());

    user.setRoles(roles == null || roles.isEmpty() ? Set.of(Role.USER) : roles);

    return userRepository.save(user);
  }

  public Optional<User> loginUser(String email, String password) {
    Optional<User> userOptional = userRepository.findByEmail(email);
    if (userOptional.isEmpty()) {
      return Optional.empty();
    }

    User user = userOptional.get();
    if (validatePassword(password, user.getPasswordHash())) {
      return Optional.of(user);
    }

    return Optional.empty();
  }

  public boolean validatePassword(String rawPassword, String storedPassword) {
    return passwordEncoder.matches(rawPassword, storedPassword);
  }

  // Get user details by username
  public User getUserByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(username));
  }

  // Get user details by email
  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public void deleteUserById(UUID id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException("User with ID " + id + " not found.");
    }
    userRepository.deleteById(id);
  }

  /**
   * Fetches all users from the database.
   * @return a list of User entities (you may want to map to DTOs)
   */
  public List<UserDTO> getAllUsers() {
    return userRepository.findAll().stream()
            .map(u -> new UserDTO(
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getRoles().stream().map(Role::name).toList(),
                    u.getCreatedAt().toString()
            ))
            .toList();
  }
}
