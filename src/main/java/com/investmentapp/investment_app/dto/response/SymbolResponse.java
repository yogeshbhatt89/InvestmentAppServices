package com.investmentapp.investment_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SymbolResponse {
  private String description;
  private String displaySymbol;
  private String symbol;
  private String type;
}
