package com.investmentapp.investment_app.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;

    public JwtAuthenticationToken(String username, Collection<? extends GrantedAuthority> authorities) {
        super(authorities); // Pass authorities to the parent constructor
        this.username = username;
        setAuthenticated(true); // Mark as authenticated since JWT is already validated
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
