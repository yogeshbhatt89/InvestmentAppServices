package com.investmentapp.investment_app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "country",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_country_code", columnNames = "code"),
                @UniqueConstraint(name = "uc_country_name", columnNames = "name")
        }
)
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2)
    private String code;        // ISO2 code, e.g. "US", "CA"

    @Column(nullable = false, length = 100)
    private String name;        // e.g. "United States", "Canada"

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
