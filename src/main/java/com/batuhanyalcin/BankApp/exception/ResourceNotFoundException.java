package com.batuhanyalcin.BankApp.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BankAppException {
    
    private static final long serialVersionUID = 1L;
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
            HttpStatus.NOT_FOUND,
            String.format("%s, %s: '%s' ile bulunamadı", resourceName, fieldName, fieldValue),
            "RESOURCE_NOT_FOUND"
        );
    }
    
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message, "RESOURCE_NOT_FOUND");
    }
} 