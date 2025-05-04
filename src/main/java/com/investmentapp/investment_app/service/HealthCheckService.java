package com.investmentapp.investment_app.service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthCheckService {

    private static final String HEALTH_CHECK_URL = "https://investmentappservices.onrender.com/health";

    private final RestTemplate restTemplate;

    public HealthCheckService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 600000) // 10 minutes in milliseconds
    public void pingHealthEndpoint() {
        try {
            restTemplate.getForObject(HEALTH_CHECK_URL, String.class);
            System.out.println("[HealthCheckService] Pinged /health endpoint at " + new java.util.Date());
        } catch (Exception e) {
            System.err.println("[HealthCheckService] Failed to ping /health endpoint: " + e.getMessage());
        }
    }
}
