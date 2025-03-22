package com.investmentapp.investment_app.service;

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

    // Generate both access and refresh tokens
    public Map<String, String> generateTokens(String username, List<String> roles) {
        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        String accessToken = jwtTokenUtil.generateAccessToken(username, authorities);
        String refreshToken = jwtTokenUtil.generateRefreshToken(username);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    // Generate only an access token
    public String generateAccessToken(String username, List<String> roles) {
        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return jwtTokenUtil.generateAccessToken(username, authorities);
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
            return jwtTokenUtil.generateAccessToken(username, roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
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
}
