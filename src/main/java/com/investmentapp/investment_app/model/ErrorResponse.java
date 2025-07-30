package com.investmentapp.investment_app.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class ErrorResponse {
  private Error error;

  public ErrorResponse(HttpStatus httpStatus, String errorDetail) {
    this.error = new Error(httpStatus.value(), httpStatus.getReasonPhrase(), errorDetail);
  }
}
