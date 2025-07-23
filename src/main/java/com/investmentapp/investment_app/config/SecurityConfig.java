package com.investmentapp.investment_app.config;

import com.investmentapp.investment_app.repository.UserRepository;
import com.investmentapp.investment_app.security.JwtAuthorizationFilter;
import com.investmentapp.investment_app.security.JwtTokenUtil;
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
public class SecurityConfig {

  private final JwtTokenUtil jwtTokenUtil;
  private final UserRepository userRepository;

  public SecurityConfig(JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
    this.jwtTokenUtil = jwtTokenUtil;
    this.userRepository = userRepository;
  }

  @Bean
  public JwtAuthorizationFilter jwtAuthorizationFilter() {
    return new JwtAuthorizationFilter(jwtTokenUtil, userRepository);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorizeRequests ->
                authorizeRequests
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/refresh")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/health")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/investments/**")
                    .permitAll()
                    .requestMatchers("/me")
                    .hasAuthority("ROLE_USER")
                    .requestMatchers(HttpMethod.GET, "/api/investments")
                    .hasAuthority("ROLE_USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/users/**")
                    .hasAuthority("ROLE_ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/users/**")
                    .hasAuthority("ROLE_ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/users")
                    .hasAuthority("ROLE_ADMIN")
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        List.of("http://localhost:5173", "https://investogram-app.netlify.app")); // Allowed origins
    configuration.setAllowedMethods(
        List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP methods
    configuration.setAllowedHeaders(List.of("*")); // Allow all headers
    configuration.setAllowCredentials(
        true); // Allow credentials (cookies, authorization headers, etc.)
    configuration.setMaxAge(3600L); // Cache preflight response for 1 hour

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // Apply CORS to all endpoints
    return source;
  }
}
