package com.batuhanyalcin.BankApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenRefreshException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TokenRefreshException(String token, String message) {
        super(String.format("Refresh token işlemi başarısız: %s için %s", token, message));
    }
} 