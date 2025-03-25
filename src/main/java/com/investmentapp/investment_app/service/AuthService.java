package com.investmentapp.investment_app.service;

import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.repository.UserRepository;
import com.investmentapp.investment_app.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;  // Inject the UserRepository here

    // Generate both access and refresh tokens
    public Map<String, String> generateTokens(User user) {
        // Generate both access and refresh tokens using the user object
        String accessToken = jwtTokenUtil.generateAccessToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getUsername()); // Assuming refresh token generation still uses username

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    // Generate only an access token
    public String generateAccessToken(User user) {
        return jwtTokenUtil.generateAccessToken(user); // Call the updated method with User
    }

    // Generate only a refresh token
    public String generateRefreshToken(String username) {
        return jwtTokenUtil.generateRefreshToken(username);
    }

    // Refresh access token using a valid refresh token
    public String refreshAccessToken(String refreshToken) {
        if (jwtTokenUtil.validateToken(refreshToken, true)) {
            String username = jwtTokenUtil.getUsernameFromToken(refreshToken, true);
            List<String> roles = jwtTokenUtil.getAuthoritiesFromToken(refreshToken).stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            // You would typically fetch the user from the database here to get the full user info
            User user = getUserByUsername(username); // Assuming you have a method to get the User object from the username
            return jwtTokenUtil.generateAccessToken(user);
        }
        throw new RuntimeException("Invalid or expired refresh token");
    }

    // Validate the access token
    public boolean validateAccessToken(String accessToken) {
        return jwtTokenUtil.validateToken(accessToken, false);
    }

    // Extract the username from the access token
    public String extractUsernameFromAccessToken(String accessToken) {
        return jwtTokenUtil.getUsernameFromToken(accessToken, false);
    }

    // Validate the refresh token
    public boolean validateRefreshToken(String refreshToken) {
        return jwtTokenUtil.validateToken(refreshToken, true);
    }

    // Extract the username from the refresh token
    public String extractUsernameFromRefreshToken(String refreshToken) {
        return jwtTokenUtil.getUsernameFromToken(refreshToken, true);
    }

    // Helper method to get a User by username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}
