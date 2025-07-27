package com.investmentapp.investment_app.model;

import com.investmentapp.investment_app.enums.TransactionType;
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
@Table(name = "transaction")
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"portfolio", "investment"})
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "portfolio_id", nullable = false)
  private Portfolio portfolio;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "investment_id", nullable = false)
  private Investment investment;

  private String stockSymbol;
  private int quantity;
  private BigDecimal price;
  private BigDecimal totalCost;

  @Enumerated(EnumType.STRING)
  private TransactionType type;

  @Column(name = "transaction_date", nullable = false, updatable = false)
  private LocalDateTime transactionDate;

  @PrePersist
  protected void onCreate() {
    this.transactionDate = LocalDateTime.now();
  }
}
