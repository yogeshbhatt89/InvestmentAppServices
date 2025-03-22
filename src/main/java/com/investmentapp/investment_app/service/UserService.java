package com.investmentapp.investment_app.service;

import com.investmentapp.investment_app.exception.EmailAlreadyExistsException;
import com.investmentapp.investment_app.model.Role;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String fullName, String email, String password, String username) {
        // Check if the email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        // Hash the password
        String passwordHash = passwordEncoder.encode(password);

        // Create a new User object
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setUsername(username);
        user.setCreatedAt(LocalDateTime.now());

        if (user.getRoles() == null) {
            user.setRoles(Set.of(Role.USER));  // Assign default role (USER)
        }
        // Save the user to the database
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
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
    //Get user details by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
