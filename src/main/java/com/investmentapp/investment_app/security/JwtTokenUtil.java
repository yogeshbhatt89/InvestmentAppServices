package com.investmentapp.investment_app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenUtil {
    // Generate a secure 512-bit key for HS512 every time the application runs
    private SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private static final long EXPIRATION_TIME = 3600000; // 1 hour in milliseconds

    // Generate a JWT token
    public String generateToken(String username) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME); // Expiration time set here

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expirationDate) // Set the expiration date
                .signWith(secretKey)  // Sign the token with the generated key
                .compact();
    }

    // Extract username from the token
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // Validate the token
    public boolean validateToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return !claims.getExpiration().before(new Date());
    }

    // Extract claims from the token using the JwtParserBuilder for version 0.12.x
    private Claims getClaimsFromToken(String token) {
        JwtParser jwtParser = Jwts.parser()
                .setSigningKey(secretKey)
                .build();

        return jwtParser.parseClaimsJws(token).getBody();
    }
}
