package com.investmentapp.investment_app.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    // Getters and setters
    private String errorCode;
  private String errorMessage;

  public ErrorResponse(String errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

}
