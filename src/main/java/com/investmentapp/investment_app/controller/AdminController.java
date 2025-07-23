package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.exception.UserNotFoundException;
import com.investmentapp.investment_app.service.UserService;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/users")
public class AdminController {

  private final UserService userService;

  public AdminController(UserService userService) {
    this.userService = userService;
  }

  /*
   Endpoint to delete a user by ID.
     Accessible only by users with the ADMIN role.
     Returns a success message with HTTP status 204 if the user is successfully deleted.
  */
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

  /*
     Endpoint to fetch all users.
     Accessible only by users with the ADMIN role.
     Returns a list of all users in the system.
     If an error occurs, returns a 500 Internal Server Error response with error details.
  */

  @GetMapping
  public ResponseEntity<?> getAllUsers() {
    try {
      var users = userService.getAllUsers(); // returns List<UserDto> or List<User>

      Map<String, Object> successBody =
          Map.of(
              "success",
              Map.of(
                  "code",
                  200,
                  "message",
                  "Fetched all users",
                  "details",
                  "Total users: " + users.size()),
              "data",
              users);

      return ResponseEntity.ok(successBody);

    } catch (Exception ex) {
      Map<String, Object> errorBody =
          Map.of(
              "error",
              Map.of("code", 500, "message", "Internal server error", "details", ex.getMessage()));
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }
  }
}
