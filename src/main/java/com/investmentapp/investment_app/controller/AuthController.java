package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.exception.InvalidCredentialsException;
import com.investmentapp.investment_app.exception.RefreshTokenMissingException;
import com.investmentapp.investment_app.exception.UserNotFoundException;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.security.JwtTokenUtil;
import com.investmentapp.investment_app.service.AuthService;
import com.investmentapp.investment_app.service.UserService;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired private JwtTokenUtil jwtTokenUtil;

  @Autowired private AuthService authService;

  @Autowired private UserService userService;

  // Register user
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
    User user =
        userService.registerUser(
            registerRequest.getFullName(),
            registerRequest.getEmail(),
            registerRequest.getPassword(),
            registerRequest.getUsername());
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  // Login user and return JWT tokens
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    Optional<User> userOptional =
        userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());

    if (userOptional.isPresent()) {
      User user = userOptional.get();

      // Directly pass the user object to generateTokens
      Map<String, String> tokens = authService.generateTokens(user); // Pass user object

      return ResponseEntity.ok(tokens);
    }
    throw new InvalidCredentialsException("Invalid credentials");
  }

  // Refresh access token
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
    String refreshToken = request.get("refreshToken");

    if (refreshToken == null) {
      throw new RefreshTokenMissingException("Refresh token is required");
    }

    String newAccessToken = authService.refreshAccessToken(refreshToken);
    return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
  }

  @GetMapping("/me")
  public ResponseEntity<?> getLoggedInUser(
      @RequestHeader("Authorization") String authorizationHeader) {
    try {
      // Check if Authorization header is present and starts with "Bearer "
      if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        throw new InvalidCredentialsException("Authorization header is missing or invalid");
      }

      // Extract token from the header
      String token = authorizationHeader.replace("Bearer ", "");

      // Validate token
      if (!jwtTokenUtil.validateToken(token)) {
        throw new InvalidCredentialsException("Invalid or expired token");
      }

      // Extract username (or email) from the token
      String username = jwtTokenUtil.getUsernameFromToken(token);
      // Fetch user details by username (email, or whatever you use)
      Optional<User> userOptional = Optional.ofNullable(userService.getUserByUsername(username));

      if (userOptional.isPresent()) {
        User user = userOptional.get();
        return ResponseEntity.ok(user); // No more role check here
      } else {
        throw new UserNotFoundException("User not found");
      }
    } catch (InvalidCredentialsException | UserNotFoundException ex) {
      throw ex;
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to retrieve user details: " + e.getMessage());
    }
  }
}
