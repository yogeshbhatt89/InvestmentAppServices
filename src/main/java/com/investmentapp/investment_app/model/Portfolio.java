package com.investmentapp.investment_app.model;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mock_portfolios")
@EqualsAndHashCode(of = "id")
@ToString(exclude = "user")
public class Portfolio implements Serializable {
  @Serial private static final long serialVersionUID = 2L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private BigDecimal initialBalance;

  private BigDecimal currentBalance;

  private String riskTolerance;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  // Copy constructor
  public Portfolio(Portfolio other) {
    if (other != null) {
      this.id = other.id;
      this.name = other.name;
      this.initialBalance =
          other.initialBalance != null ? new BigDecimal(other.initialBalance.toString()) : null;
      this.currentBalance =
          other.currentBalance != null ? new BigDecimal(other.currentBalance.toString()) : null;
      this.riskTolerance = other.riskTolerance;
      this.user = other.user;
      this.createdAt = other.createdAt != null ? LocalDateTime.from(other.createdAt) : null;
    }
  }

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }
}
