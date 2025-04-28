package com.investmentapp.investment_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Global CORS configuration
        registry.addMapping("/**")  // Apply CORS to all endpoints
                .allowedOrigins("http://localhost:5173", "https://investogram-app.netlify.app/")  // Allow specific origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Allow specific methods
                .allowedHeaders("*")  // Allow all headers
                .allowCredentials(true)  // Allow credentials (cookies, authorization headers, etc.)
                .maxAge(3600);  // Pre-flight cache duration in seconds
    }
}
