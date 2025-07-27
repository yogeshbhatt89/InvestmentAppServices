package com.investmentapp.investment_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "investment")
public class Investment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;        // e.g., Apple, Bitcoin
  private String symbol;      // e.g., AAPL
  private String type;        // e.g., stock, crypto
  private String description; // optional description
  private BigDecimal price;   // current price

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }
}
