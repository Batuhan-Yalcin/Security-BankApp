package com.batuhanyalcin.BankApp.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BankAppException {
    
    private static final long serialVersionUID = 1L;
    
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message, "BAD_REQUEST");
    }
    
    public BadRequestException(String message, String errorCode) {
        super(HttpStatus.BAD_REQUEST, message, errorCode);
    }
} 