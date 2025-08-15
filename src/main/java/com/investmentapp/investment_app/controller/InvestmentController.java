package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.client.FinnhubClient;
import com.investmentapp.investment_app.dto.response.*;
import com.investmentapp.investment_app.model.ApiResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  public ResponseEntity<?> searchInvestment(@RequestParam String ticker) {
    logger.info("Received request for /api/investments/quote with ticker: {}", ticker);

    try {
      StockQuoteResponse quote = finnhubClient.getQuote(ticker);

      if (quote != null) {
        logger.info("Returning stock data for ticker: {}", ticker);
        return ResponseEntity.ok(
            ApiResponse.success(
                quote, HttpStatus.OK.value(), "Quote fetched", "ticker: " + ticker));
      } else {
        logger.error("Failed to retrieve stock data for ticker: {}", ticker);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ApiResponse.error(
                    HttpStatus.NOT_FOUND.value(),
                    "RESOURCE_NOT_FOUND",
                    "Quote not found for ticker: " + ticker));
      }
    } catch (RuntimeException e) {
      logger.error("Error occurred while fetching stock quote: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ApiResponse.error(
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  "INTERNAL_SERVER_ERROR",
                  e.getMessage()));
    }
  }

  @GetMapping("/batchQuotes")
  public ResponseEntity<?> batchQuotes(@RequestParam String symbols) {
    logger.info("Received batch quote request for symbols: {}", symbols);

    if (symbols == null || symbols.trim().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(
              ApiResponse.error(
                  HttpStatus.BAD_REQUEST.value(),
                  "BAD_REQUEST",
                  "Symbols parameter cannot be empty"));
    }

    String[] symbolArray = symbols.split(",");
    Map<String, Object> result = new HashMap<>();
    List<Map<String, Object>> successfulQuotes = new ArrayList<>();
    List<String> failedSymbols = new ArrayList<>();

    for (String symbol : symbolArray) {
      String trimmedSymbol = symbol.trim();
      if (!trimmedSymbol.isEmpty()) {
        try {
          StockQuoteResponse quote = finnhubClient.getQuote(trimmedSymbol);
          if (quote != null) {
            Map<String, Object> quoteResponse = new HashMap<>();
            quoteResponse.put("symbol", trimmedSymbol);
            quoteResponse.put("data", quote);
            successfulQuotes.add(quoteResponse);
          } else {
            failedSymbols.add(trimmedSymbol);
          }
        } catch (RuntimeException e) {
          logger.error("Error fetching quote for symbol {}: {}", trimmedSymbol, e.getMessage());
          failedSymbols.add(trimmedSymbol);
        }
      }
    }

    result.put("quotes", successfulQuotes);

    if (!failedSymbols.isEmpty()) {
      result.put("failedSymbols", failedSymbols);
      result.put("message", "Some quotes could not be fetched");

      if (successfulQuotes.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ApiResponse.error(
                    HttpStatus.NOT_FOUND.value(),
                    "NO_QUOTES_FOUND",
                    "No quotes could be found for the provided symbols"));
      }

      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
          .body(
              ApiResponse.success(
                  result,
                  HttpStatus.PARTIAL_CONTENT.value(),
                  "Partial content: Some quotes could not be fetched",
                  "Processed "
                      + successfulQuotes.size()
                      + " out of "
                      + symbolArray.length
                      + " symbols"));
    }

    return ResponseEntity.ok(
        ApiResponse.success(
            result,
            HttpStatus.OK.value(),
            "Batch quotes fetched successfully",
            "Processed " + successfulQuotes.size() + " symbols"));
  }

  @GetMapping("/searchSymbols")
  public ResponseEntity<?> searchSymbols(
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
      return ResponseEntity.ok(
          ApiResponse.success(
              symbols,
              HttpStatus.OK.value(),
              "Fetched symbols",
              "count: " + (symbols != null ? symbols.size() : 0)));
    } catch (RuntimeException e) {
      logger.error("Error occurred: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ApiResponse.error(
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  "INTERNAL_SERVER_ERROR",
                  e.getMessage()));
    }
  }

  @GetMapping("/symbolLookup")
  public ResponseEntity<?> symbolLookup(
      @RequestParam String q, @RequestParam(required = false) String exchange) {
    logger.info("Received request for /api/investments/symbolLookup with query: {}", q);

    try {
      SymbolLookupResponse response = finnhubClient.symbolLookup(q, exchange);

      if (response.getResult().isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ApiResponse.error(
                    HttpStatus.NOT_FOUND.value(),
                    "RESOURCE_NOT_FOUND",
                    "No symbols found for query: " + q));
      }
      return ResponseEntity.ok(
          ApiResponse.success(
              response,
              HttpStatus.OK.value(),
              "Symbol lookup successful",
              "query: " + q + (exchange != null ? ", exchange: " + exchange : "")));
    } catch (RuntimeException e) {
      logger.error("Error occurred: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ApiResponse.error(
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  "INTERNAL_SERVER_ERROR",
                  e.getMessage()));
    }
  }

  @GetMapping("/batchSymbolLookup")
  public ResponseEntity<?> batchSymbolLookup(
      @RequestParam String symbols, @RequestParam(required = false) String exchange) {
    logger.info("Received batch symbol lookup request for symbols: {}", symbols);

    if (symbols == null || symbols.trim().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(
              ApiResponse.error(
                  HttpStatus.BAD_REQUEST.value(),
                  "BAD_REQUEST",
                  "Symbols parameter cannot be empty"));
    }

    String[] symbolArray = symbols.split(",");
    Map<String, Object> result = new HashMap<>();
    List<Object> responses = new ArrayList<>();
    List<String> failedSymbols = new ArrayList<>();

    for (String symbol : symbolArray) {
      String trimmedSymbol = symbol.trim();
      if (!trimmedSymbol.isEmpty()) {
        try {
          SymbolLookupResponse response = finnhubClient.symbolLookup(trimmedSymbol, exchange);
          if (!response.getResult().isEmpty()) {
            Map<String, Object> symbolResponse = new HashMap<>();
            symbolResponse.put("symbol", trimmedSymbol);
            symbolResponse.put("data", response);
            responses.add(symbolResponse);
          } else {
            failedSymbols.add(trimmedSymbol);
          }
        } catch (RuntimeException e) {
          logger.error("Error looking up symbol {}: {}", trimmedSymbol, e.getMessage());
          failedSymbols.add(trimmedSymbol);
        }
      }
    }

    result.put("successfulLookups", responses);

    if (!failedSymbols.isEmpty()) {
      result.put("failedSymbols", failedSymbols);
      result.put("message", "Some symbols could not be found or had errors");

      if (responses.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ApiResponse.error(
                    HttpStatus.NOT_FOUND.value(),
                    "NO_SYMBOLS_FOUND",
                    "No symbols could be found for the provided list"));
      }

      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
          .body(
              ApiResponse.success(
                  result,
                  HttpStatus.PARTIAL_CONTENT.value(),
                  "Partial content: Some symbols could not be found",
                  "Processed " + responses.size() + " out of " + symbolArray.length + " symbols"));
    }

    return ResponseEntity.ok(
        ApiResponse.success(
            result,
            HttpStatus.OK.value(),
            "Batch symbol lookup successful",
            "Processed " + responses.size() + " symbols"));
  }

  @GetMapping("/recommendationTrends")
  public ResponseEntity<?> getRecommendationTrends(@RequestParam String ticker) {
    logger.info(
        "Received request for /api/investments/recommendationTrends with ticker: {}", ticker);

    try {
      List<RecommendationTrendResponse> trends = finnhubClient.getRecommendationTrends(ticker);

      if (trends != null && !trends.isEmpty()) {
        logger.info("Returning recommendation trends for ticker: {}", ticker);
        return ResponseEntity.ok(
            ApiResponse.success(
                trends,
                HttpStatus.OK.value(),
                "Recommendation trends fetched",
                "ticker: " + ticker + ", count: " + trends.size()));
      } else {
        logger.error("No recommendation trends found for ticker: {}", ticker);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ApiResponse.error(
                    HttpStatus.NOT_FOUND.value(),
                    "RESOURCE_NOT_FOUND",
                    "No recommendation trends found for ticker: " + ticker));
      }
    } catch (RuntimeException e) {
      logger.error("Error occurred while fetching recommendation trends: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ApiResponse.error(
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  "INTERNAL_SERVER_ERROR",
                  e.getMessage()));
    }
  }

  @GetMapping("/marketStatus")
  public ResponseEntity<?> getMarketStatus(@RequestParam String exchange) {
    logger.info("Received request for /api/investments/marketStatus with exchange: {}", exchange);

    try {
      MarketStatusResponse marketStatus = finnhubClient.getMarketStatus(exchange);

      if (marketStatus != null) {
        logger.info("Returning market status for exchange: {}", exchange);
        return ResponseEntity.ok(
            ApiResponse.success(
                marketStatus,
                HttpStatus.OK.value(),
                "Market status fetched",
                "exchange: " + exchange));
      } else {
        logger.error("No market status found for exchange: {}", exchange);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ApiResponse.error(
                    HttpStatus.NOT_FOUND.value(),
                    "RESOURCE_NOT_FOUND",
                    "No market status found for exchange: " + exchange));
      }
    } catch (RuntimeException e) {
      logger.error("Error occurred while fetching market status: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ApiResponse.error(
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  "INTERNAL_SERVER_ERROR",
                  e.getMessage()));
    }
  }

  @GetMapping("/marketNews")
  public ResponseEntity<?> getMarketNews(
      @RequestParam String category, @RequestParam(required = false) Long minId) {
    logger.info(
        "Received request for /api/investments/marketNews with category: {}, minId: {}",
        category,
        minId);

    try {
      List<MarketNewsResponse> news = finnhubClient.getMarketNews(category.toLowerCase(), minId);

      if (news != null && !news.isEmpty()) {
        logger.info("Returning {} news items for category: {}", news.size(), category);
        return ResponseEntity.ok(
            ApiResponse.success(
                news,
                HttpStatus.OK.value(),
                "Market news fetched successfully",
                "category: " + category + (minId != null ? ", minId: " + minId : "")));
      } else {
        logger.error("No market news found for category: {}", category);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ApiResponse.error(
                    HttpStatus.NOT_FOUND.value(),
                    "RESOURCE_NOT_FOUND",
                    "No market news found for category: " + category));
      }
    } catch (IllegalArgumentException e) {
      logger.error("Invalid request parameter: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(
              ApiResponse.error(
                  HttpStatus.BAD_REQUEST.value(), "INVALID_PARAMETER", e.getMessage()));
    } catch (RuntimeException e) {
      logger.error("Error occurred while fetching market news: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ApiResponse.error(
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  "INTERNAL_SERVER_ERROR",
                  e.getMessage()));
    }
  }

  @GetMapping("/companyProfile")
  public ResponseEntity<?> getCompanyProfile(
      @RequestParam(required = false) String symbol,
      @RequestParam(required = false) String isin,
      @RequestParam(required = false) String cusip) {

    try {
      // Call the FinnhubClient to get the company profile
      CompanyProfileResponse companyProfile = finnhubClient.getCompanyProfile(symbol, isin, cusip);

      // Return the company profile wrapped in a ResponseEntity with HTTP status 200
      return ResponseEntity.ok(
          ApiResponse.success(
              companyProfile,
              HttpStatus.OK.value(),
              "Company profile fetched",
              (symbol != null ? "symbol: " + symbol + " " : "")
                  + (isin != null ? "isin: " + isin + " " : "")
                  + (cusip != null ? "cusip: " + cusip : "")));
    } catch (Exception e) {
      // Log and handle any error that occurs during the process
      logger.error("Error fetching company profile", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ApiResponse.error(
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  "INTERNAL_SERVER_ERROR",
                  e.getMessage()));
    }
  }

  @GetMapping("/companyNews")
  public ResponseEntity<?> getCompanyNews(
      @RequestParam String symbol, // The company symbol (required)
      @RequestParam String from, // Start date in format YYYY-MM-DD (required)
      @RequestParam String to // End date in format YYYY-MM-DD (required)
      ) {

    try {
      // Call the FinnhubClient to get the company news
      List<CompanyNewsResponse> companyNews = finnhubClient.getCompanyNews(symbol, from, to);

      // Return the company news wrapped in a ResponseEntity with HTTP status 200
      return ResponseEntity.ok(
          ApiResponse.success(
              companyNews,
              HttpStatus.OK.value(),
              "Company news fetched",
              "symbol: " + symbol + ", count: " + (companyNews != null ? companyNews.size() : 0)));
    } catch (Exception e) {
      // Log and handle any error that occurs during the process
      logger.error("Error fetching company news", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ApiResponse.error(
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  "INTERNAL_SERVER_ERROR",
                  e.getMessage()));
    }
  }
}
