package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.exception.UserNotFoundException;
import com.investmentapp.investment_app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/users")
public class AdminController {

  private final UserService userService;

  public AdminController(UserService userService) {
    this.userService = userService;
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
    try {
      userService.deleteUserById(id);

      Map<String, Object> successBody =
          Map.of(
              "success",
              Map.of(
                  "code",
                  204,
                  "message",
                  "User successfully deleted",
                  "details",
                  "User with ID " + id + " was deleted"));

      return ResponseEntity.status(HttpStatus.OK).body(successBody);

    } catch (UserNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(
              Map.of(
                  "error",
                  Map.of(
                      "code", 404, "message", "Resource not found", "details", ex.getMessage())));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              Map.of(
                  "error",
                  Map.of(
                      "code",
                      500,
                      "message",
                      "Internal server error",
                      "details",
                      ex.getMessage())));
    }
  }
}
