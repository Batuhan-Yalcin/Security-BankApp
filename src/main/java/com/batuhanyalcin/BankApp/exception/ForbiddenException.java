package com.batuhanyalcin.BankApp.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BankAppException {
    
    private static final long serialVersionUID = 1L;
    
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message, "FORBIDDEN");
    }
    
    public ForbiddenException(String message, String errorCode) {
        super(HttpStatus.FORBIDDEN, message, errorCode);
    }
} 