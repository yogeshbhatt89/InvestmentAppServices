package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.client.FinnhubClient;
import com.investmentapp.investment_app.dto.response.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
// @PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/api/investments")
public class InvestmentController {
  private static final Logger logger = LoggerFactory.getLogger(InvestmentController.class);

  private final FinnhubClient finnhubClient;

  public InvestmentController(FinnhubClient finnhubClient) {
    this.finnhubClient = finnhubClient;
  }

  @GetMapping("/quote")
  public ResponseEntity<StockQuoteResponse> searchInvestment(@RequestParam String ticker) {
    logger.info("Received request for /api/investments/search with ticker: {}", ticker);

    try {
      StockQuoteResponse quote = finnhubClient.getQuote(ticker);

      if (quote != null) {
        logger.info("Returning stock data for ticker: {}", ticker);
        return ResponseEntity.ok(quote);
      } else {
        logger.error("Failed to retrieve stock data for ticker: {}", ticker);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StockQuoteResponse());
      }
    } catch (RuntimeException e) {
      logger.error("Error occurred while fetching stock quote: {}", e.getMessage());
      StockQuoteResponse errorResponse = new StockQuoteResponse();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
  }

  @GetMapping("/searchSymbols")
  public ResponseEntity<List<StockSymbolResponse>> searchSymbols(
      @RequestParam String exchange,
      @RequestParam(required = false) String mic,
      @RequestParam(required = false) String securityType,
      @RequestParam(required = false) String currency,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(defaultValue = "0") int offset) {
    logger.info("Received request for /api/investments/searchSymbols with exchange: {}", exchange);

    try {
      List<StockSymbolResponse> symbols =
          finnhubClient.getStockSymbols(exchange, mic, securityType, currency, limit, offset);
      return ResponseEntity.ok(symbols);
    } catch (RuntimeException e) {
      logger.error("Error occurred: {}", e.getMessage());
      List<StockSymbolResponse> errorResponse = new ArrayList<>();
      StockSymbolResponse errorDTO = new StockSymbolResponse();
      errorDTO.setDescription(e.getMessage());
      errorResponse.add(errorDTO);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
  }

  @GetMapping("/symbolLookup")
  public ResponseEntity<SymbolLookupResponse> symbolLookup(
      @RequestParam String q, @RequestParam(required = false) String exchange) {
    logger.info("Received request for /api/investments/symbolLookup with query: {}", q);

    try {
      SymbolLookupResponse response = finnhubClient.symbolLookup(q, exchange);

      if (response.getResult().isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      logger.error("Error occurred: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              new SymbolLookupResponse(
                  0,
                  Collections.singletonList(
                      SymbolResponse.builder()
                          .description("Error occurred: " + e.getMessage())
                          .build())));
    }
  }

  @GetMapping("/recommendationTrends")
  public ResponseEntity<List<RecommendationTrendResponse>> getRecommendationTrends(
      @RequestParam String ticker) {
    logger.info(
        "Received request for /api/investments/recommendationTrends with ticker: {}", ticker);

    try {
      List<RecommendationTrendResponse> trends = finnhubClient.getRecommendationTrends(ticker);

      if (trends != null && !trends.isEmpty()) {
        logger.info("Returning recommendation trends for ticker: {}", ticker);
        return ResponseEntity.ok(trends);
      } else {
        logger.error("No recommendation trends found for ticker: {}", ticker);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
      }
    } catch (RuntimeException e) {
      logger.error("Error occurred while fetching recommendation trends: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
    }
  }

  @GetMapping("/marketStatus")
  public ResponseEntity<MarketStatusResponse> getMarketStatus(@RequestParam String exchange) {
    logger.info("Received request for /api/investments/marketStatus with exchange: {}", exchange);

    try {
      MarketStatusResponse marketStatus = finnhubClient.getMarketStatus(exchange);

      if (marketStatus != null) {
        logger.info("Returning market status for exchange: {}", exchange);
        return ResponseEntity.ok(marketStatus);
      } else {
        logger.error("No market status found for exchange: {}", exchange);
        return ResponseEntity.notFound().build();
      }
    } catch (RuntimeException e) {
      logger.error("Error occurred while fetching market status: {}", e.getMessage());
      MarketStatusResponse errorResponse = new MarketStatusResponse("Error: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
  }

  @GetMapping("/companyProfile")
  public ResponseEntity<CompanyProfileResponse> getCompanyProfile(
      @RequestParam(required = false) String symbol,
      @RequestParam(required = false) String isin,
      @RequestParam(required = false) String cusip) {

    try {
      // Call the FinnhubClient to get the company profile
      CompanyProfileResponse companyProfile = finnhubClient.getCompanyProfile(symbol, isin, cusip);

      // Return the company profile wrapped in a ResponseEntity with HTTP status 200
      return ResponseEntity.ok(companyProfile);
    } catch (Exception e) {
      // Log and handle any error that occurs during the process
      logger.error("Error fetching company profile", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/companyNews")
  public ResponseEntity<List<CompanyNewsResponse>> getCompanyNews(
      @RequestParam String symbol, // The company symbol (required)
      @RequestParam String from, // Start date in format YYYY-MM-DD (required)
      @RequestParam String to // End date in format YYYY-MM-DD (required)
      ) {

    try {
      // Call the FinnhubClient to get the company news
      List<CompanyNewsResponse> companyNews = finnhubClient.getCompanyNews(symbol, from, to);

      // Return the company news wrapped in a ResponseEntity with HTTP status 200
      return ResponseEntity.ok(companyNews);
    } catch (Exception e) {
      // Log and handle any error that occurs during the process
      logger.error("Error fetching company news", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
