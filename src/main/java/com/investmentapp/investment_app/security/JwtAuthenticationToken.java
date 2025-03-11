package com.investmentapp.investment_app.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;

    public JwtAuthenticationToken(String username) {
        super(null); // Passes null because the authentication is already done using the JWT
        this.username = username;
        setAuthenticated(true); // Set as authenticated
    }

    @Override
    public Object getCredentials() {
        return null; // No credentials needed for JWT-based authentication
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }
}
