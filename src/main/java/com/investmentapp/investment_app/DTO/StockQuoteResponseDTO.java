package com.investmentapp.investment_app.DTO;

import lombok.Data;

@Data
public class StockQuoteResponseDTO {

  private double currentPrice;
  private double priceChange;
  private double percentChange;
  private double highPrice;
  private double lowPrice;
  private double openPrice;
  private double prevClosePrice;
  private long timestamp;

  public double getCurrentPrice() {
    return currentPrice;
  }

  public void setCurrentPrice(double currentPrice) {
    this.currentPrice = currentPrice;
  }

  public double getPriceChange() {
    return priceChange;
  }

  public void setPriceChange(double priceChange) {
    this.priceChange = priceChange;
  }

  public double getPercentChange() {
    return percentChange;
  }

  public void setPercentChange(double percentChange) {
    this.percentChange = percentChange;
  }

  public double getHighPrice() {
    return highPrice;
  }

  public void setHighPrice(double highPrice) {
    this.highPrice = highPrice;
  }

  public double getLowPrice() {
    return lowPrice;
  }

  public void setLowPrice(double lowPrice) {
    this.lowPrice = lowPrice;
  }

  public double getOpenPrice() {
    return openPrice;
  }

  public void setOpenPrice(double openPrice) {
    this.openPrice = openPrice;
  }

  public double getPrevClosePrice() {
    return prevClosePrice;
  }

  public void setPrevClosePrice(double prevClosePrice) {
    this.prevClosePrice = prevClosePrice;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
