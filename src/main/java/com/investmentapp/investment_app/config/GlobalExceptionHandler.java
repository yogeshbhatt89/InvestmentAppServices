package com.investmentapp.investment_app.config;

import com.investmentapp.investment_app.exception.AccessDeniedException;
import com.investmentapp.investment_app.exception.InsufficientBalanceException;
import com.investmentapp.investment_app.exception.NoHoldingsToSellException;
import com.investmentapp.investment_app.model.ErrorResponse;
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
  public ResponseEntity<Object> handleNoHoldingsToSell(NoHoldingsToSellException ex) {
    // Create a response object with a custom message
    ErrorResponse errorResponse = new ErrorResponse("400", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InsufficientBalanceException.class) // Add this handler
  public ResponseEntity<Object> handleInsufficientBalance(InsufficientBalanceException ex) {
    // Create a response object with a custom message
    ErrorResponse errorResponse = new ErrorResponse("400", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AccessDeniedException.class) // Add this handler
  public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
    // Create a response object with a custom message
    ErrorResponse errorResponse = new ErrorResponse("400", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
    // Handle other argument exceptions
    ErrorResponse errorResponse = new ErrorResponse("400", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationErrors(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGenericException(Exception ex) {
    // Handle generic exceptions
    ErrorResponse errorResponse = new ErrorResponse("400", "An unexpected error occurred");
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
