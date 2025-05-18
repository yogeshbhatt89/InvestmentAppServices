package com.investmentapp.investment_app.security;

import com.investmentapp.investment_app.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {

  private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

  private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 15; // 15 minutes
  private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7 days

  public String generateAccessToken(User user) {
    List<String> roles =
        user.getRolesAsString().stream().map(role -> "ROLE_" + role).collect(Collectors.toList());

    return Jwts.builder()
        .subject(user.getEmail())
        .claim("username", user.getUsername())
        .claim("roles", roles)
        .claim("fullName", user.getFullName())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
        .signWith(SECRET_KEY)
        .compact();
  }

  public String generateRefreshToken(User user) {
    return Jwts.builder()
        .subject(user.getEmail())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
        .signWith(SECRET_KEY)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Claims claims = extractAllClaims(token);
      Date expiration = claims.getExpiration();
      System.out.println("Token expiration: " + expiration);
      return expiration.after(new Date());
    } catch (ExpiredJwtException e) {
      System.out.println("Token expired: " + e.getMessage());
    } catch (JwtException e) {
      System.out.println("Invalid token: " + e.getMessage());
    }
    return false;
  }

  public String getEmailFromToken(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public String getUsernameFromToken(String token) {
    // Extract the username claim from the token
    return extractClaim(token, claims -> claims.get("username", String.class));
  }

  public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
    Claims claims = extractAllClaims(token);
    List<String> roles = claims.get("roles", List.class);

    return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    try {
      JwtParser parser = Jwts.parser().verifyWith(SECRET_KEY).build();

      return parser.parseSignedClaims(token).getPayload();
    } catch (ExpiredJwtException e) {
      System.out.println("Token has expired: " + e.getMessage());
      throw e;
    } catch (JwtException e) {
      System.out.println("Invalid token: " + e.getMessage());
      throw e;
    }
  }
}
