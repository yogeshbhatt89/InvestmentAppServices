package com.investmentapp.investment_app.service;

import com.investmentapp.investment_app.exception.InvalidCredentialsException;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.repository.UserRepository;
import com.investmentapp.investment_app.security.JwtTokenUtil;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  @Autowired private JwtTokenUtil jwtTokenUtil;

  @Autowired private UserRepository userRepository;

  // Generate both access and refresh tokens
  public Map<String, String> generateTokens(User user) {
    String accessToken = jwtTokenUtil.generateAccessToken(user);
    String refreshToken = jwtTokenUtil.generateRefreshToken(user);

    Map<String, String> tokens = new HashMap<>();
    tokens.put("accessToken", accessToken);
    tokens.put("refreshToken", refreshToken);
    return tokens;
  }

  // Generate only an access token
  public String generateAccessToken(User user) {
    return jwtTokenUtil.generateAccessToken(user);
  }

  // Generate only a refresh token
  public String generateRefreshToken(User user) {
    return jwtTokenUtil.generateRefreshToken(user);
  }

  // Refresh access token using a valid refresh token
  public String refreshAccessToken(String refreshToken) {
    if (jwtTokenUtil.validateToken(refreshToken)) {
      String email = jwtTokenUtil.getEmailFromToken(refreshToken);
      User user = getUserByEmail(email);

      if (user == null) {
        throw new InvalidCredentialsException("User not found for email: " + email);
      }
      return jwtTokenUtil.generateAccessToken(user);
    }
    throw new InvalidCredentialsException("Invalid or expired refresh token");
  }

  // Validate the access token
  public boolean validateAccessToken(String accessToken) {
    return jwtTokenUtil.validateToken(accessToken);
  }

  // Extract the email from the access token
  public String extractEmailFromAccessToken(String accessToken) {
    return jwtTokenUtil.getEmailFromToken(accessToken);
  }

  // Validate the refresh token
  public boolean validateRefreshToken(String refreshToken) {
    return jwtTokenUtil.validateToken(refreshToken);
  }

  // Extract the email from the refresh token
  public String extractEmailFromRefreshToken(String refreshToken) {
    return jwtTokenUtil.getEmailFromToken(refreshToken);
  }

  // Helper method to get a User by email
  public User getUserByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
  }
}
