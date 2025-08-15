package com.investmentapp.investment_app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.investmentapp.investment_app.enums.Role;
import jakarta.persistence.*;
import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {
  @Serial private static final long serialVersionUID = 1L;

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
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate birthday;

  // Using wrapper classes to handle the transient nature of these fields
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_id")
  @Transient
  private Country country;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "language_id")
  @Transient
  private Language language;

  @Column(name = "is_active")
  private Boolean isActive = Boolean.TRUE;

  @Column(name = "profile_pictureurl")
  private String profilePictureUrl;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  @Column(name = "last_login_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime lastLoginAt;

  @Column(name = "password_changed_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime passwordChangedAt;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Enumerated(EnumType.STRING)
  private Set<Role> roles = new HashSet<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Portfolio> portfolios = new ArrayList<>();

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
    return roles.stream().map(Enum::name).collect(Collectors.toList());
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

  public Set<Role> getRoles() {
    return roles != null ? new HashSet<>(roles) : new HashSet<>();
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
