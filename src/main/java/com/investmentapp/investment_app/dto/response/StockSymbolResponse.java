package com.investmentapp.investment_app.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockSymbolResponse {
  private String currency;
  private String description;
  private String displaySymbol;
  private String figi;
  private String isin;
  private String mic;
  private String shareClassFIGI;
  private String symbol;
  private String symbol2;
  private String type;
}
