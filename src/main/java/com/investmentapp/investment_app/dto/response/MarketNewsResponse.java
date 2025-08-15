package com.investmentapp.investment_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for market news response from Finnhub API. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketNewsResponse {
  private String category;
  private long datetime;
  private String headline;
  private long id;
  private String image;
  private String related;
  private String source;
  private String summary;
  private String url;
}
