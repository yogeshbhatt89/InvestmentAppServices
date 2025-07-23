package com.investmentapp.investment_app.DTO;

import java.util.List;
import java.util.UUID;

public class UserDTO {
  private UUID id;
  private String username;
  private String email;
  private List<String> roles;
  private String createdAt;

  public UserDTO() {}

  public UserDTO(UUID id, String username, String email, List<String> roles, String createdAt) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }
}
