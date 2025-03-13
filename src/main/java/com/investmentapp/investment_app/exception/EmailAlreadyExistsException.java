package com.investmentapp.investment_app.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("The email " + email + " already exists.");
    }
}