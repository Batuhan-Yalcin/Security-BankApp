package com.batuhanyalcin.BankApp.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends BankAppException {
    
    private static final long serialVersionUID = 1L;
    
    public BusinessRuleException(String message) {
        super(HttpStatus.BAD_REQUEST, message, "BUSINESS_RULE_VIOLATION");
    }
    
    public BusinessRuleException(String message, String errorCode) {
        super(HttpStatus.BAD_REQUEST, message, errorCode);
    }
} 