package com.investmentapp.investment_app.config;

import com.investmentapp.investment_app.repository.UserRepository;
import com.investmentapp.investment_app.security.JwtAuthorizationFilter;
import com.investmentapp.investment_app.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenUtil jwtTokenUtil;
  private final UserRepository userRepository;
  private final CustomAccessDeniedHandler accessDeniedHandler;

  @Value("${app.cors.allowed-origins}")
  private String[] allowedOrigins;

  @Value("${app.cors.allowed-methods}")
  private String[] allowedMethods;

  @Value("${app.cors.allowed-headers}")
  private String[] allowedHeaders;

  @Value("${app.cors.allow-credentials}")
  private boolean allowCredentials;

  @Value("${app.cors.max-age}")
  private long maxAge;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth
                    // Public endpoints
                    .requestMatchers("/api/auth/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/health", "/api/investments/**" ,"/api/lookups/**")
                    .permitAll()

                    // User-only endpoints
                    .requestMatchers("/me")
                    .hasRole("USER")

                    // Admin-only endpoints
                    .requestMatchers("/api/users/**")
                    .hasRole("ADMIN")

                    // Fallback: require authentication
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler))
        .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public JwtAuthorizationFilter jwtAuthorizationFilter() {
    return new JwtAuthorizationFilter(jwtTokenUtil, userRepository);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(allowedOrigins));
    config.setAllowedMethods(List.of(allowedMethods));
    config.setAllowedHeaders(List.of(allowedHeaders));
    config.setAllowCredentials(allowCredentials);
    config.setMaxAge(maxAge);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
