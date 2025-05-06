package com.batuhanyalcin.BankApp.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BankAppException {
    
    private static final long serialVersionUID = 1L;
    
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message, "UNAUTHORIZED");
    }
    
    public UnauthorizedException(String message, String errorCode) {
        super(HttpStatus.UNAUTHORIZED, message, errorCode);
    }
}