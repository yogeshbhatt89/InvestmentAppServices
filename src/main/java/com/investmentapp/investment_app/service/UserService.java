package com.investmentapp.investment_app.service;

import com.investmentapp.investment_app.exception.EmailAlreadyExistsException;
import com.investmentapp.investment_app.exception.UserNotFoundException;
import com.investmentapp.investment_app.model.Role;
import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.repository.UserRepository;
import com.investmentapp.investment_app.request.CreateUserRequest;
import com.investmentapp.investment_app.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    public User registerUser(String firstName, String email, String password, String username, Optional<String> isAdmin) {
//        // Check if the email already exists
//        if (userRepository.findByEmail(email).isPresent()) {
//            throw new EmailAlreadyExistsException("Email already exists!");
//        }
//
//        // Hash the password
//        String passwordHash = passwordEncoder.encode(password);
//
//        // Create a new User object
//        User user = new User();
//        user.setFirstName(firstName);
//        user.setEmail(email);
//        user.setPasswordHash(passwordHash);
//        user.setUsername(username);
//        user.setCreatedAt(LocalDateTime.now().toString());
//        user.getRoles().add(Role.USER);
//
//        if(isAdmin.isPresent()) {
//            System.out.println("I made it here wohoo");
//            System.out.println(user.getRoles());
//            user.getRoles().add(Role.ADMIN);
//            System.out.println(user.getRoles());
//        }
//
//        // Save the user to the database
//        return userRepository.save(user);
//    }


    public Optional<User> loginUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        if (validatePassword(password, user.getPasswordHash())) {
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public boolean validatePassword(String rawPassword, String storedPassword) {
        return passwordEncoder.matches(rawPassword, storedPassword);
    }

    // Get user details by username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }


    //Get user details by email
    public Optional<User> getUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }


    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        String passwordHash = passwordEncoder.encode(request.getPassword());
        boolean isAdmin = Boolean.TRUE.equals(request.isAdmin());

        return Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.getEmail()))
                .map(req -> {
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setPasswordHash(passwordHash);
                    user.setUsername(request.getUsername());
                    user.getRoles().add(Role.USER);
                    if(isAdmin) {
                        user.getRoles().add(Role.ADMIN);
                    }
                    return userRepository.save(user);
                }).orElseThrow(() -> new UserNotFoundException("User already exists!"));
    }

    @Override
    public User updateUser(UserUpdateRequest request, UUID userId) {
        return userRepository.findById(userId).map(existingUser -> {
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            existingUser.setBirthday(request.getBirthday());
            existingUser.setCountry(request.getCountry());
            existingUser.setLanguage(request.getLanguage());
            existingUser.setProfilePictureURL(request.getProfilePictureURL());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    @Override
    public void deleteUser(UUID userId) {
        System.out.print("UserId: " + userId);
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()) {
            User user1 = user.get();
            user1.getRoles().clear();
            userRepository.save(user1);
        }
        userRepository.findById(userId).ifPresentOrElse(userRepository :: delete, () -> {
            throw new UserNotFoundException("User not found!");
                });

    }
}
