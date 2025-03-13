package com.investmentapp.investment_app.service;

import com.investmentapp.investment_app.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Generate both access and refresh tokens
    public Map<String, String> generateTokens(String username) {
        String accessToken = jwtTokenUtil.generateAccessToken(username);
        String refreshToken = jwtTokenUtil.generateRefreshToken(username);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    // Generate only an access token
    public String generateAccessToken(String username) {
        return jwtTokenUtil.generateAccessToken(username);
    }

    // Generate only a refresh token
    public String generateRefreshToken(String username) {
        return jwtTokenUtil.generateRefreshToken(username);
    }

    // Refresh access token using a valid refresh token
    public String refreshAccessToken(String refreshToken) {
        if (jwtTokenUtil.validateToken(refreshToken, true)) {
            String username = jwtTokenUtil.getUsernameFromToken(refreshToken, true);
            return jwtTokenUtil.generateAccessToken(username);
        }
        throw new RuntimeException("Invalid or expired refresh token");
    }

    // Validate the access token
    public boolean validateAccessToken(String accessToken) {
        return jwtTokenUtil.validateToken(accessToken, false);
    }

    // Extract the username (or email) from the access token
    public String extractUsernameFromAccessToken(String accessToken) {
        return jwtTokenUtil.getUsernameFromToken(accessToken, false);
    }

    // Validate the refresh token
    public boolean validateRefreshToken(String refreshToken) {
        return jwtTokenUtil.validateToken(refreshToken, true);
    }

    // Extract the username (or email) from the refresh token
    public String extractUsernameFromRefreshToken(String refreshToken) {
        return jwtTokenUtil.getUsernameFromToken(refreshToken, true);
    }
}
