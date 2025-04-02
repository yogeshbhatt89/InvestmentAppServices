package com.investmentapp.investment_app.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.investmentapp.investment_app.DTO.*;
import com.investmentapp.investment_app.controller.SymbolLookupResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class FinnhubClient {

    private static final Logger logger = LoggerFactory.getLogger(FinnhubClient.class);
    private final HttpClient client;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public FinnhubClient(@Value("${finnhub.api.key}") String apiKey) {
        this.client = HttpClient.newHttpClient();
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
    }

    public StockQuoteResponseDTO getQuote(String symbol) {
        String url = String.format("https://finnhub.io/api/v1/quote?symbol=%s&token=%s", symbol, apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.info("Finnhub API response: {}", response.body());
                Map<String, Object> jsonResponse = objectMapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});

                if (jsonResponse.containsKey("error")) {
                    logger.error("Finnhub API error: {}", jsonResponse.get("error"));
                    return null;
                }
                return mapToDTO(jsonResponse);
            } else {
                logger.error("Finnhub API error: {} - {}", response.statusCode(), response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return null;
        }
    }

    public List<StockSymbolDTO> getStockSymbols(String exchange, String mic, String securityType, String currency, int limit, int offset) {
        String url = String.format("https://finnhub.io/api/v1/stock/symbol?exchange=%s&mic=%s&securityType=%s&currency=%s&limit=%d&offset=%d&token=%s",
                exchange, mic, securityType, currency, limit, offset, apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if status is 200
            if (response.statusCode() == 200) {
                logger.info("Finnhub API response: {}", response.body());

                // Check for specific error in the response body, e.g., "You don't have access to this resource."
                if (response.body().contains("error") || response.body().contains("You don't have access to this resource")) {
                    logger.error("Finnhub API returned an error: {}", response.body());
                    throw new RuntimeException("Finnhub API error: " + response.body());
                }

                // Return the response if no errors
                return objectMapper.readValue(response.body(), new TypeReference<List<StockSymbolDTO>>() {});
            } else {
                // If status is not 200, log and throw an exception
                logger.error("Finnhub API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("Error from Finnhub API: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    public SymbolLookupResponse symbolLookup(String query, String exchange) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String encodedExchange = URLEncoder.encode(exchange, StandardCharsets.UTF_8);
        String url = String.format("https://finnhub.io/api/v1/search?q=%s&exchange=%s&token=%s", encodedQuery, encodedExchange, apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.info("Finnhub API response: {}", response.body());

                if (response.body().contains("error")) {
                    logger.error("Finnhub API error: {}", response.body());
                    throw new RuntimeException("Finnhub API error: " + response.body());
                }

                return objectMapper.readValue(response.body(), SymbolLookupResponse.class);
            } else {
                logger.error("Finnhub API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("Error from Finnhub API: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    public List<RecommendationTrendDTO> getRecommendationTrends(String symbol) {
        String url = String.format("https://finnhub.io/api/v1/stock/recommendation?symbol=%s&token=%s", symbol, apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.info("Finnhub API response: {}", response.body());
                List<RecommendationTrendDTO> trends = objectMapper.readValue(response.body(), new TypeReference<List<RecommendationTrendDTO>>() {});
                return trends;
            } else {
                logger.error("Finnhub API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("Error from Finnhub API: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    public MarketStatusDTO getMarketStatus(String exchange) {
        String url = String.format("https://finnhub.io/api/v1/stock/market-status?exchange=%s&token=%s", exchange, apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.info("Finnhub API response: {}", response.body());
                return objectMapper.readValue(response.body(), MarketStatusDTO.class);
            } else {
                logger.error("Finnhub API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("Error from Finnhub API: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    public CompanyProfileDTO getCompanyProfile(String symbol, String isin, String cusip) {
        String url = buildCompanyProfileUrl(symbol, isin, cusip);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), CompanyProfileDTO.class);
            } else {
                throw new RuntimeException("Error from Finnhub API: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    public List<CompanyNewsDTO> getCompanyNews(String symbol, String from, String to) {
        String url = String.format("https://finnhub.io/api/v1/company-news?symbol=%s&from=%s&to=%s&token=%s", symbol, from, to, apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.info("Finnhub API response: {}", response.body());
                // Parse the response to a List of CompanyNewsDTO
                return objectMapper.readValue(response.body(), new TypeReference<List<CompanyNewsDTO>>() {});
            } else {
                logger.error("Finnhub API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("Error from Finnhub API: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    private String buildCompanyProfileUrl(String symbol, String isin, String cusip) {
        String url = String.format("https://finnhub.io/api/v1/stock/profile2?token=%s", apiKey);

        if (symbol != null && !symbol.isEmpty()) {
            url += "&symbol=" + symbol;
        } else if (isin != null && !isin.isEmpty()) {
            url += "&isin=" + isin;
        } else if (cusip != null && !cusip.isEmpty()) {
            url += "&cusip=" + cusip;
        } else {
            throw new IllegalArgumentException("At least one of symbol, isin, or cusip must be provided.");
        }

        return url;
    }


    public StockQuoteResponseDTO mapToDTO(Map<String, Object> jsonResponse) {
        StockQuoteResponseDTO dto = new StockQuoteResponseDTO();

        // Handling the fields that can be Double
        dto.setCurrentPrice(getDouble(jsonResponse.get("c")));
        dto.setPriceChange(getDouble(jsonResponse.get("d")));
        dto.setPercentChange(getDouble(jsonResponse.get("dp")));
        dto.setHighPrice(getDouble(jsonResponse.get("h")));
        dto.setLowPrice(getDouble(jsonResponse.get("l")));
        dto.setOpenPrice(getDouble(jsonResponse.get("o")));
        dto.setPrevClosePrice(getDouble(jsonResponse.get("pc")));

        // Handling timestamp with validation
        Object timestampObj = jsonResponse.get("t");
        if (timestampObj instanceof Integer) {
            dto.setTimestamp(((Integer) timestampObj).longValue());
        } else if (timestampObj instanceof Long) {
            dto.setTimestamp((Long) timestampObj);
        } else {
            throw new IllegalArgumentException("Invalid timestamp type");
        }

        return dto;
    }
    private Double getDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        return 0.0;
    }
}
