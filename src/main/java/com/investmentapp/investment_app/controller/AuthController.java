package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.dto.request.LoginRequest;
import com.investmentapp.investment_app.dto.request.RegisterRequest;
import com.investmentapp.investment_app.dto.response.UserResponse;
import com.investmentapp.investment_app.exception.EmailAlreadyExistsException;
import com.investmentapp.investment_app.exception.UsernameAlreadyExistsException;
import com.investmentapp.investment_app.model.ApiResponse;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.security.JwtTokenUtil;
import com.investmentapp.investment_app.service.AuthService;
import com.investmentapp.investment_app.service.UserService;
import jakarta.validation.Valid;
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

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {

    try {
      User created = userService.registerUser(req);

      UserResponse resp = UserResponse.fromEntity(created);

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(
              ApiResponse.success(
                  resp, HttpStatus.CREATED.value(), "Registration successful", null));

    } catch (EmailAlreadyExistsException | UsernameAlreadyExistsException ex) {
      // 409 Conflict when email or username is already taken
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(ApiResponse.error(HttpStatus.CONFLICT.value(), "USER_EXISTS", ex.getMessage()));
    }
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

      return ResponseEntity.ok(
          ApiResponse.success(tokens, HttpStatus.OK.value(), "Login successful", null));
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(
              ApiResponse.error(
                  HttpStatus.UNAUTHORIZED.value(), "INVALID_CREDENTIALS", "Invalid credentials"));
    }
  }

  // Refresh access token
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
    String refreshToken = request.get("refreshToken");

    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(
              ApiResponse.error(
                  HttpStatus.BAD_REQUEST.value(),
                  "REFRESH_TOKEN_REQUIRED",
                  "Refresh token is required"));
    }

    try {
      String newAccessToken = authService.refreshAccessToken(refreshToken);
      return ResponseEntity.ok(
          ApiResponse.success(
              Map.of("accessToken", newAccessToken),
              HttpStatus.OK.value(),
              "Token refreshed",
              null));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(
              ApiResponse.error(
                  HttpStatus.UNAUTHORIZED.value(), "INVALID_REFRESH_TOKEN", e.getMessage()));
    }
  }

  @GetMapping("/me")
  public ResponseEntity<?> getLoggedInUser(
      @RequestHeader("Authorization") String authorizationHeader) {
    try {
      // Check if Authorization header is present and starts with "Bearer "
      if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                ApiResponse.error(
                    HttpStatus.UNAUTHORIZED.value(),
                    "INVALID_AUTH_HEADER",
                    "Authorization header is missing or invalid"));
      }

      // Extract token from the header
      String token = authorizationHeader.replace("Bearer ", "");

      // Validate token
      if (!jwtTokenUtil.validateToken(token)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                ApiResponse.error(
                    HttpStatus.UNAUTHORIZED.value(), "INVALID_TOKEN", "Invalid or expired token"));
      }

      // Extract username (or email) from the token
      String username = jwtTokenUtil.getUsernameFromToken(token);
      // Fetch user details by username (email, or whatever you use)
      Optional<User> userOptional = Optional.ofNullable(userService.getUserByUsername(username));

      if (userOptional.isPresent()) {
        User user = userOptional.get();
        return ResponseEntity.ok(
            ApiResponse.success(user, HttpStatus.OK.value(), "User details fetched", null));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                ApiResponse.error(
                    HttpStatus.UNAUTHORIZED.value(), "USER_NOT_FOUND", "User not found"));
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ApiResponse.error(
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  "USER_DETAILS_FAILED",
                  "Failed to retrieve user details: " + e.getMessage()));
    }
  }
}
