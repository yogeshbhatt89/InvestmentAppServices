package com.investmentapp.investment_app.exception;

public class NoHoldingsToSellException extends RuntimeException {
    public NoHoldingsToSellException(String message) {
        super(message);
    }
}
