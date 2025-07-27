package com.investmentapp.investment_app.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.investmentapp.investment_app.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {

  @NotBlank(message = "First name is required")
  @Size(max = 50, message = "First name must be at most 50 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 50, message = "Last name must be at most 50 characters")
  private String lastName;

  @Email(message = "Email should be valid")
  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "Username is required")
  @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
  private String username;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;

  @Past(message = "Birthday must be in the past")
  private LocalDate birthday;

  private Long countryId;

  private Long languageId;

  /**
   * Optional: if empty or null, UserService will default to Role.USER
   */
  private Set<Role> roles;
}
