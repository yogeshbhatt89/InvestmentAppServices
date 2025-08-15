package com.investmentapp.investment_app.config;

import com.investmentapp.investment_app.exception.AccessDeniedException;
import com.investmentapp.investment_app.exception.InsufficientBalanceException;
import com.investmentapp.investment_app.exception.NoHoldingsToSellException;
import com.investmentapp.investment_app.model.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoHoldingsToSellException.class)
  public ResponseEntity<ApiResponse<Object>> handleNoHoldingsToSell(NoHoldingsToSellException ex) {
    return new ResponseEntity<>(
        ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "NO_HOLDINGS", ex.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InsufficientBalanceException.class) // Add this handler
  public ResponseEntity<ApiResponse<Object>> handleInsufficientBalance(
      InsufficientBalanceException ex) {
    return new ResponseEntity<>(
        ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "INSUFFICIENT_BALANCE", ex.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AccessDeniedException.class) // Add this handler
  public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
    return new ResponseEntity<>(
        ApiResponse.error(HttpStatus.FORBIDDEN.value(), "ACCESS_DENIED", ex.getMessage()),
        HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
    return new ResponseEntity<>(
        ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "INVALID_ARGUMENT", ex.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return new ResponseEntity<>(
        ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILED", errors.toString()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
    return new ResponseEntity<>(
        ApiResponse.error(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_ERROR",
            "An unexpected error occurred"),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
