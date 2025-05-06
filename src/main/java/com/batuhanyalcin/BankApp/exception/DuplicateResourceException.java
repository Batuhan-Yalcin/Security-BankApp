package com.batuhanyalcin.BankApp.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BankAppException {
    
    private static final long serialVersionUID = 1L;
    
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(
            HttpStatus.CONFLICT,
            String.format("%s zaten mevcut: %s = '%s'", resourceName, fieldName, fieldValue),
            "DUPLICATE_RESOURCE"
        );
    }
    
    public DuplicateResourceException(String message) {
        super(HttpStatus.CONFLICT, message, "DUPLICATE_RESOURCE");
    }
} 