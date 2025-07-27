package com.investmentapp.investment_app.dto.request;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequest {
  private UUID id;
  private String username;
  private String email;
  private List<String> roles;
  private String createdAt;

  public UserRequest() {}

  public UserRequest(UUID id, String username, String email, List<String> roles, String createdAt) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
    this.createdAt = createdAt;
  }
}
