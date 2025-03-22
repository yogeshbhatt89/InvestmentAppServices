package com.investmentapp.investment_app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthorizationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        extractToken(request).ifPresent(token -> {
            try {
                if (jwtTokenUtil.validateToken(token, false)) {
                    setAuthentication(token);
                }
            } catch (Exception e) {
                logger.warn("JWT validation failed: " + e.getMessage());
            }
        });

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer "))
                ? Optional.of(header.substring(7))
                : Optional.empty();
    }

    private void setAuthentication(String token) {
        String username = jwtTokenUtil.getUsernameFromToken(token, false);
        Collection<? extends GrantedAuthority> authorities = jwtTokenUtil.getAuthoritiesFromToken(token);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
