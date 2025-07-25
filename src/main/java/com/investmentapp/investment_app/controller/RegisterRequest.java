package com.investmentapp.investment_app.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.investmentapp.investment_app.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {

  @NotEmpty(message = "Full Name is required")
  private String fullName;

  @Email(message = "Email should be valid")
  @NotEmpty(message = "Email is required")
  private String email;

  @NotEmpty(message = "Password is required")
  private String password;

  @NotEmpty(message = "Username is required")
  @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
  private String username;

  private Set<Role> roles;

  // Getters and setters

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
}
