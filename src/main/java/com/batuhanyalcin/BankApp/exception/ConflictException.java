package com.batuhanyalcin.BankApp.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BankAppException {
    
    private static final long serialVersionUID = 1L;
    
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message, "CONFLICT");
    }
    
    public ConflictException(String message, String errorCode) {
        super(HttpStatus.CONFLICT, message, errorCode);
    }
}