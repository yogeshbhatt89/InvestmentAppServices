package com.investmentapp.investment_app.dto.response;

import lombok.Data;

@Data
public class StockQuoteResponse {
  private double currentPrice;
  private double priceChange;
  private double percentChange;
  private double highPrice;
  private double lowPrice;
  private double openPrice;
  private double prevClosePrice;
  private long timestamp;
}
