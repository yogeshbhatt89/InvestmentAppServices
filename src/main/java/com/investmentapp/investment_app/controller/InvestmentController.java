package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.DTO.*;
import com.investmentapp.investment_app.client.FinnhubClient;
import com.investmentapp.investment_app.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/api/investments")
public class InvestmentController {
    private static final Logger logger = LoggerFactory.getLogger(InvestmentController.class);

    private final FinnhubClient finnhubClient;

    public InvestmentController(FinnhubClient finnhubClient) {
        this.finnhubClient = finnhubClient;
    }


    @GetMapping("/quote")
    public ResponseEntity<StockQuoteResponseDTO> searchInvestment(@RequestParam String ticker, @AuthenticationPrincipal User user) {
        logger.info("Received request for /api/investments/search with ticker: {} for user: {}", ticker, user.getEmail());

        try {
            StockQuoteResponseDTO quote = finnhubClient.getQuote(ticker);

            if (quote != null) {
                logger.info("Returning stock data for ticker: {}", ticker);
                return ResponseEntity.ok(quote);
            } else {
                logger.error("Failed to retrieve stock data for ticker: {}", ticker);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StockQuoteResponseDTO());
            }
        } catch (RuntimeException e) {
            logger.error("Error occurred while fetching stock quote: {}", e.getMessage());
            StockQuoteResponseDTO errorResponse = new StockQuoteResponseDTO();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/searchSymbols")
    public ResponseEntity<List<StockSymbolDTO>> searchSymbols(@RequestParam String exchange,
                                                              @RequestParam(required = false) String mic,
                                                              @RequestParam(required = false) String securityType,
                                                              @RequestParam(required = false) String currency,
                                                              @RequestParam(defaultValue = "10") int limit,
                                                              @RequestParam(defaultValue = "0") int offset,
                                                              @AuthenticationPrincipal User user) {
        logger.info("Received request for /api/investments/searchSymbols with exchange: {} and user: {}", exchange, user.getEmail());

        try {
            List<StockSymbolDTO> symbols = finnhubClient.getStockSymbols(exchange, mic, securityType, currency, limit, offset);
            return ResponseEntity.ok(symbols);
        } catch (RuntimeException e) {
            logger.error("Error occurred: {}", e.getMessage());
            List<StockSymbolDTO> errorResponse = new ArrayList<>();
            StockSymbolDTO errorDTO = new StockSymbolDTO();
            errorDTO.setDescription(e.getMessage());
            errorResponse.add(errorDTO);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/symbolLookup")
    public ResponseEntity<SymbolLookupResponse> symbolLookup(@RequestParam String q,
                                                             @RequestParam(required = false) String exchange,
                                                             @AuthenticationPrincipal User user) {
        logger.info("Received request for /api/investments/symbolLookup with query: {} and user: {}", q, user.getEmail());

        try {
            SymbolLookupResponse response = finnhubClient.symbolLookup(q, exchange);

            if (response.getResult().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SymbolLookupResponse(0, Collections.singletonList(new SymbolDTO("Error occurred: " + e.getMessage()))));
        }
    }

    @GetMapping("/recommendationTrends")
    public ResponseEntity<List<RecommendationTrendDTO>> getRecommendationTrends(@RequestParam String ticker, @AuthenticationPrincipal User user) {
        logger.info("Received request for /api/investments/recommendationTrends with ticker: {} for user: {}", ticker, user.getEmail());

        try {
            List<RecommendationTrendDTO> trends = finnhubClient.getRecommendationTrends(ticker);

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
    public ResponseEntity<MarketStatusDTO> getMarketStatus(@RequestParam String exchange, @AuthenticationPrincipal User user) {
        logger.info("Received request for /api/investments/marketStatus with exchange: {} for user: {}", exchange, user.getEmail());

        try {
            MarketStatusDTO marketStatus = finnhubClient.getMarketStatus(exchange);

            if (marketStatus != null) {
                logger.info("Returning market status for exchange: {}", exchange);
                return ResponseEntity.ok(marketStatus);
            } else {
                logger.error("No market status found for exchange: {}", exchange);
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            logger.error("Error occurred while fetching market status: {}", e.getMessage());
            MarketStatusDTO errorResponse = new MarketStatusDTO("Error: " + e.getMessage()); // Include error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/companyProfile")
    public ResponseEntity<CompanyProfileDTO> getCompanyProfile(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) String isin,
            @RequestParam(required = false) String cusip,
            @AuthenticationPrincipal UserDetails principal) {

        try {
            // Log the authenticated user's username for debugging purposes
            logger.info("Authenticated user: {}", principal.getUsername());

            // Call the FinnhubClient to get the company profile
            CompanyProfileDTO companyProfile = finnhubClient.getCompanyProfile(symbol, isin, cusip);

            // Return the company profile wrapped in a ResponseEntity with HTTP status 200
            return ResponseEntity.ok(companyProfile);
        } catch (Exception e) {
            // Log and handle any error that occurs during the process
            logger.error("Error fetching company profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
