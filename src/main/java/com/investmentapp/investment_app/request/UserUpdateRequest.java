package com.investmentapp.investment_app.request;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String firstName;
    private String lastName;
    private String birthday;
    private String country; //default US
    private String language; //default English
    private String profilePictureURL;
}
