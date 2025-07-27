package com.investmentapp.investment_app.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.investmentapp.investment_app.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

  private Long portfolioId;
  private String stockSymbol;
  private TransactionType type;
  private int quantity;
  private BigDecimal price;

  @JsonProperty(access = Access.READ_ONLY)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Setter(AccessLevel.NONE)
  private LocalDateTime transactionDate;
}
