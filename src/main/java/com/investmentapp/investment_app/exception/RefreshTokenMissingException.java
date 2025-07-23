package com.investmentapp.investment_app.exception;

public class RefreshTokenMissingException extends RuntimeException {
  public RefreshTokenMissingException(String message) {
    super(message);
  }
}
