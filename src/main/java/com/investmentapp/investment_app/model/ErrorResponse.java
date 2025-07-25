package com.investmentapp.investment_app.model;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
  private Error error;

  public ErrorResponse(HttpStatus httpStatus, String errorDetail) {
    this.error = new Error(httpStatus.value(), httpStatus.getReasonPhrase(), errorDetail);
  }

  // Getters and setters

  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
  }
}
