package com.investmentapp.investment_app.model;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
  private SuccessMeta success;
  private ErrorMeta error;
  private T data;
  private Instant timestamp;

  public static <T> ApiResponse<T> success(T data, int code, String message, String details) {
    ApiResponse<T> resp = new ApiResponse<>();
    resp.success = new SuccessMeta(code, message, details);
    resp.data = data;
    resp.timestamp = Instant.now();
    return resp;
  }

  public static <T> ApiResponse<T> error(int code, String message, String details) {
    ApiResponse<T> resp = new ApiResponse<>();
    resp.error = new ErrorMeta(code, message, details);
    resp.data = null;
    resp.timestamp = Instant.now();
    return resp;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class SuccessMeta {
    private int code;
    private String message;
    private String details;

    public SuccessMeta(int code, String message, String details) {
      this.code = code;
      this.message = message;
      this.details = details;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class ErrorMeta {
    private int code;
    private String message;
    private String details;

    public ErrorMeta(int code, String message, String details) {
      this.code = code;
      this.message = message;
      this.details = details;
    }
  }
}
