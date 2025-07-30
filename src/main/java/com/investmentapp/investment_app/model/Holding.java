package com.investmentapp.investment_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "holding")
public class Holding implements Serializable {
  @Serial private static final long serialVersionUID = 3L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "portfolio_id", nullable = false)
  @JsonIgnore
  @ToString.Exclude
  private Portfolio portfolio;

  private String stockSymbol;
  private int quantity;
  private BigDecimal averagePrice;
}
