package com.investmentapp.investment_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.investmentapp.investment_app.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "UUID DEFAULT uuid_generate_v4()")
  private UUID id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false)
  @JsonIgnore
  private String passwordHash;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "birthday", columnDefinition = "DATE")
  private LocalDate birthday;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_id")
  private Country country;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "language_id")
  private Language language;

  @Column(name = "is_active")
  private Boolean isActive = Boolean.TRUE;

  @Column(name = "profile_pictureurl")
  private String profilePictureUrl;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Column(name = "password_changed_at")
  private LocalDateTime passwordChangedAt;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Enumerated(EnumType.STRING)
  private Set<Role> roles = new HashSet<>();

  @PrePersist
  protected void onCreate() {
    // let Hibernate fill createdAt/updatedAt, but ensure default role
    if (roles.isEmpty()) {
      roles.add(Role.USER);
    }
  }

  @Transient
  @JsonProperty("full_name")
  public String getFullName() {
    String fn = Optional.ofNullable(firstName).orElse("");
    String ln = Optional.ofNullable(lastName).orElse("");
    return (fn + " " + ln).trim();
  }

  public List<String> getRolesAsString() {
    return roles.stream()
            .map(Enum::name)
            .collect(Collectors.toList());
  }

  // --- UserDetails implementation ---
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
            .map(r -> new SimpleGrantedAuthority(r.name()))
            .collect(Collectors.toList());
  }

  @Override
  public String getPassword() {
    return passwordHash;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return Boolean.TRUE.equals(isActive);
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return Boolean.TRUE.equals(isActive);
  }
}
