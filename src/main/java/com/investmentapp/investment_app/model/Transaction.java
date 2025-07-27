package com.investmentapp.investment_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.investmentapp.investment_app.enums.TransactionType;
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
@Table(name = "transaction")
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"portfolio", "investment"})
public class Transaction implements Serializable {
  @Serial private static final long serialVersionUID = 4L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "portfolio_id", nullable = false)
  @JsonIgnore
  private Portfolio portfolio;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "investment_id", nullable = false)
  @JsonIgnore
  private transient Investment investment;

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
