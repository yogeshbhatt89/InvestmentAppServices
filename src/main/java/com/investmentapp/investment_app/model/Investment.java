package com.investmentapp.investment_app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

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

  private String name; // e.g., Apple, Bitcoin
  private String symbol; // e.g., AAPL
  private String type; // e.g., stock, crypto
  private String description; // optional description
  private BigDecimal price; // current price

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  // Copy constructor
  public Investment(Investment other) {
    if (other != null) {
      this.id = other.id;
      this.name = other.name;
      this.symbol = other.symbol;
      this.type = other.type;
      this.description = other.description;
      this.price = other.price != null ? new BigDecimal(other.price.toString()) : null;
      this.createdAt = other.createdAt != null ? LocalDateTime.from(other.createdAt) : null;
    }
  }

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }
}
