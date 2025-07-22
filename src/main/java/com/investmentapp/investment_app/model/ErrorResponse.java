package com.investmentapp.investment_app.model;

public class ErrorResponse {
  private Error error;

  public ErrorResponse(Integer errorCode, String errorMessage, String errorDetail) {
    this.error = new Error(errorCode, errorMessage, errorDetail);
  }

  // Getters and setters

  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
  }
}
