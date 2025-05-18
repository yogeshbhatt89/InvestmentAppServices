package com.investmentapp.investment_app.service;

import com.investmentapp.investment_app.model.User;
import com.investmentapp.investment_app.request.CreateUserRequest;
import com.investmentapp.investment_app.request.UserUpdateRequest;

import java.util.UUID;

public interface IUserService {

    User getUserById(UUID userId);
    User createUser(CreateUserRequest request);
    User updateUser(UserUpdateRequest request, UUID userId);
    void deleteUser(UUID userId);


}
