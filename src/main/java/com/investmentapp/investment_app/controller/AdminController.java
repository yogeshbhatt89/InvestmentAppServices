package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.exception.UserNotFoundException;
import com.investmentapp.investment_app.model.ApiResponse;
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

      return ResponseEntity.status(HttpStatus.OK)
          .body(
              ApiResponse.success(
                  Map.of("deleted", true, "id", id.toString()),
                  HttpStatus.OK.value(),
                  "User successfully deleted",
                  "User with ID " + id + " was deleted"));

    } catch (UserNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(
              ApiResponse.error(
                  HttpStatus.NOT_FOUND.value(), "RESOURCE_NOT_FOUND", ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ApiResponse.error(
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  "INTERNAL_SERVER_ERROR",
                  ex.getMessage()));
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

      return ResponseEntity.ok(
          ApiResponse.success(
              users, HttpStatus.OK.value(), "Fetched all users", "Total users: " + users.size()));

    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ApiResponse.error(
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  "INTERNAL_SERVER_ERROR",
                  ex.getMessage()));
    }
  }
}
