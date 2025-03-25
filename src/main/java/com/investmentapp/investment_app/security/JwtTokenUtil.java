package com.investmentapp.investment_app.security;

import com.investmentapp.investment_app.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    // Generate the secret key using HS512 algorithm
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 15; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7 days

    // Generate access token with roles
    public String generateAccessToken(User user) {
        // Extract roles from the user object
        List<String> roles = user.getRolesAsString(); // Assuming getRolesAsString() returns List<String>

        // Generate the JWT token with username and roles as claims
        return Jwts.builder()
                .setSubject(user.getUsername()) // Set username as the subject
                .claim("roles", roles)  // Store roles inside JWT
                .claim("email", user.getEmail()) // Add email as a claim
                .claim("fullName", user.getFullName()) // Add full name as a claim, if needed
                .setIssuedAt(new Date()) // Set the issued time of the token
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))  // Set token expiration time
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)  // Sign the token with a secret key
                .compact();  // Create and return the token
    }



    // Generate refresh token (roles are not needed in refresh tokens)
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // Validate token
    public boolean validateToken(String token, boolean isRefreshToken) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    // Get the username from the token
    public String getUsernameFromToken(String token, boolean isRefreshToken) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract authorities (roles) from the token
    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = extractAllClaims(token);
        List<String> roles = claims.get("roles", List.class);

        if (roles == null) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // Extract a specific claim from the token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parser()  // Updated method
                .setSigningKey(SECRET_KEY)
                .build();  // Using builder pattern
        Jws<Claims> claimsJws = parser.parseClaimsJws(token);
        return claimsJws.getBody();
    }
}


