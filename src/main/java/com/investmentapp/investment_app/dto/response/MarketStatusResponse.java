package com.investmentapp.investment_app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MarketStatusResponse {
  private String exchange;
  private String holiday;
  @JsonProperty("isOpen")
  private boolean isOpen;
  private String session;
  private String timezone;
  private long t;
  private String errorMessage;

  /** Construct a DTO carrying only an error message. */
  public MarketStatusResponse(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
