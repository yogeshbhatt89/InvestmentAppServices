package com.investmentapp.investment_app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "global_settings")
@Getter
@Setter
@NoArgsConstructor
public class GlobalSettings {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "maintenance_enabled", nullable = false)
  private boolean maintenanceEnabled = false;

  @Column(name = "global_alert_enabled", nullable = false)
  private boolean globalAlertEnabled = false;

  @Column(name = "global_alert_message")
  private String globalAlertMessage;

  @Column(name = "global_alert_severity")
  private String globalAlertSeverity; // INFO, WARN, ERROR

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
