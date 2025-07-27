package com.investmentapp.investment_app.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioRequest {

  private Long id;
  private String name;

  @Min(value = 0, message = "Initial balance must be non-negative")
  private BigDecimal initialBalance;

  private BigDecimal currentBalance;
  private String riskTolerance;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;
}
