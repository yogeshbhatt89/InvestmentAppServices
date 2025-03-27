package com.investmentapp.investment_app.security;

import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.repository.UserRepository;
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
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    public JwtAuthorizationFilter(JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        extractToken(request).ifPresent(token -> {
            try {
                System.out.println("JWT Filter invoked");
                if (jwtTokenUtil.validateToken(token)) {
                    String email = jwtTokenUtil.getEmailFromToken(token);
                    Optional<User> userOptional = userRepository.findByEmail(email);

                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        System.out.println("User is : " + user);
                        setAuthentication(user, token);  // Proceed with authentication
                    }
                }
            } catch (Exception e) {
                logger.warn("JWT validation failed: " + e.getMessage());
            }
        });

        System.out.println("Passing control to the next filter in the chain");
        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            System.out.println("Extracted Token: " + token);
            return Optional.of(token);
        }
        return Optional.empty();
    }

    private void setAuthentication(User user, String token) {
        // Use JwtTokenUtil to extract roles from the token
        List<GrantedAuthority> authorities = jwtTokenUtil.getAuthoritiesFromToken(token);

        // Pass the User object as the principal
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, token, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("Authentication set: " + SecurityContextHolder.getContext().getAuthentication());
    }
}