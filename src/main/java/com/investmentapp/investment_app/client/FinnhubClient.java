package com.investmentapp.investment_app.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.investmentapp.investment_app.dto.response.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

  public StockQuoteResponse getQuote(String symbol) {
    String url =
        String.format("https://finnhub.io/api/v1/quote?symbol=%s&token=%s", symbol, apiKey);
    Map<String, Object> jsonResponse = sendRequest(url);

    if (jsonResponse != null && !jsonResponse.containsKey("error")) {
      return mapToDTO(jsonResponse);
    }
    return null;
  }

  public List<StockSymbolResponse> getStockSymbols(
      String exchange, String mic, String securityType, String currency, int limit, int offset) {
    String url =
        String.format(
            "https://finnhub.io/api/v1/stock/symbol?exchange=%s&mic=%s&securityType=%s&currency=%s&limit=%d&offset=%d&token=%s",
            exchange, mic, securityType, currency, limit, offset, apiKey);
    return sendRequest(url, new TypeReference<List<StockSymbolResponse>>() {});
  }

  public SymbolLookupResponse symbolLookup(String query, String exchange) {
    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
    String encodedExchange = exchange != null ? URLEncoder.encode(exchange, StandardCharsets.UTF_8) : "";
    String url =
        String.format(
            "https://finnhub.io/api/v1/search?q=%s&exchange=%s&token=%s",
            encodedQuery, encodedExchange, apiKey);
    return sendRequest(url, SymbolLookupResponse.class);
  }

  public List<RecommendationTrendResponse> getRecommendationTrends(String symbol) {
    String url =
        String.format(
            "https://finnhub.io/api/v1/stock/recommendation?symbol=%s&token=%s", symbol, apiKey);
    return sendRequest(url, new TypeReference<List<RecommendationTrendResponse>>() {});
  }

  public MarketStatusResponse getMarketStatus(String exchange) {
    String url =
        String.format(
            "https://finnhub.io/api/v1/stock/market-status?exchange=%s&token=%s", exchange, apiKey);
    return sendRequest(url, MarketStatusResponse.class);
  }

  public CompanyProfileResponse getCompanyProfile(String symbol, String isin, String cusip) {
    String url = buildCompanyProfileUrl(symbol, isin, cusip);
    return sendRequest(url, CompanyProfileResponse.class);
  }

  public List<CompanyNewsResponse> getCompanyNews(String symbol, String from, String to) {
    String url =
        String.format(
            "https://finnhub.io/api/v1/company-news?symbol=%s&from=%s&to=%s&token=%s",
            symbol, from, to, apiKey);
    return sendRequest(url, new TypeReference<List<CompanyNewsResponse>>() {});
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
      throw new IllegalArgumentException(
          "At least one of symbol, isin, or cusip must be provided.");
    }

    return url;
  }

  // General request handler for GET requests returning a Map<String, Object>
  private Map<String, Object> sendRequest(String url) {
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        logger.info("Finnhub API response: {}", response.body());
        return objectMapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
      } else {
        logger.error("Finnhub API error: {} - {}", response.statusCode(), response.body());
        throw new RuntimeException("Error from Finnhub API: " + response.body());
      }
    } catch (IOException | InterruptedException e) {
      logger.error("Unexpected error: {}", e.getMessage(), e);
      throw new RuntimeException("Unexpected error: " + e.getMessage());
    }
  }

  // General request handler for GET requests returning a single DTO
  private <T> T sendRequest(String url, Class<T> dtoClass) {
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        logger.info("Finnhub API response: {}", response.body());
        return objectMapper.readValue(response.body(), dtoClass);
      } else {
        logger.error("Finnhub API error: {} - {}", response.statusCode(), response.body());
        throw new RuntimeException("Error from Finnhub API: " + response.body());
      }
    } catch (IOException | InterruptedException e) {
      logger.error("Unexpected error: {}", e.getMessage(), e);
      throw new RuntimeException("Unexpected error: " + e.getMessage());
    }
  }

  // General method for GET requests returning a list of DTOs
  private <T> List<T> sendRequest(String url, TypeReference<List<T>> typeReference) {
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        logger.info("Finnhub API response: {}", response.body());
        return objectMapper.readValue(response.body(), typeReference);
      } else {
        logger.error("Finnhub API error: {} - {}", response.statusCode(), response.body());
        throw new RuntimeException("Error from Finnhub API: " + response.body());
      }
    } catch (IOException | InterruptedException e) {
      logger.error("Unexpected error: {}", e.getMessage(), e);
      throw new RuntimeException("Unexpected error: " + e.getMessage());
    }
  }

  // General method for mapping response to DTO
  private StockQuoteResponse mapToDTO(Map<String, Object> jsonResponse) {
    StockQuoteResponse dto = new StockQuoteResponse();

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

  /**
   * Fetches market news for the specified category.
   *
   * @param category The category of news to fetch. Must be one of: general, forex, crypto, merger.
   * @param minId Optional minimum news ID to fetch. Only news with ID greater than this will be returned.
   * @return List of market news items.
   * @throws IllegalArgumentException if an invalid category is provided.
   */
  public List<MarketNewsResponse> getMarketNews(String category, Long minId) {
    // Validate category
    if (!List.of("general", "forex", "crypto", "merger").contains(category.toLowerCase())) {
      throw new IllegalArgumentException(
          "Invalid category. Must be one of: general, forex, crypto, merger");
    }

    // Build URL with required parameters
    String url = String.format(
        "https://finnhub.io/api/v1/news?category=%s&token=%s",
        category.toLowerCase(),
        apiKey
    );

    // Add optional minId parameter if provided
    if (minId != null && minId > 0) {
      url += "&minId=" + minId;
    }

    return sendRequest(url, new TypeReference<List<MarketNewsResponse>>() {});
  }

  /**
   * Overloaded method to fetch market news without minId parameter.
   *
   * @param category The category of news to fetch.
   * @return List of market news items.
   */
  public List<MarketNewsResponse> getMarketNews(String category) {
    return getMarketNews(category, null);
  }
}
