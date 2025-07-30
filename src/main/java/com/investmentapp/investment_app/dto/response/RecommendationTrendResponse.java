package com.investmentapp.investment_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationTrendResponse {
  private int buy;
  private int hold;
  private String period;
  private int sell;
  private int strongBuy;
  private int strongSell;
  private String symbol;
}
