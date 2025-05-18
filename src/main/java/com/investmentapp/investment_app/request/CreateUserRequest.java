package com.investmentapp.investment_app.request;

import com.investmentapp.investment_app.model.Role;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import java.util.Set;

@Data
public class CreateUserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String username;

    private boolean isAdmin;
}
