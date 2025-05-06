package com.batuhanyalcin.BankApp.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BankAppException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final HttpStatus status;
    private final String message;
    private final String errorCode;
    
    public BankAppException(HttpStatus status, String message, String errorCode) {
        super(message);
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
    }
    
    public BankAppException(HttpStatus status, String message) {
        this(status, message, "BANK_APP_ERROR");
    }
} 