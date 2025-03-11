package com.investmentapp.investment_app.service;

import com.investmentapp.investment_app.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Method to generate JWT token for the user
    public String generateJwtToken(String username) {
        return jwtTokenUtil.generateToken(username);  // Call to the generateToken method in JwtTokenUtil
    }
}
