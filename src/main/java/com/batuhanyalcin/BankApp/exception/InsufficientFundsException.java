package com.batuhanyalcin.BankApp.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends BankAppException {
    
    private static final long serialVersionUID = 1L;
    
    public InsufficientFundsException(String accountNumber, String requestedAmount, String currentBalance) {
        super(
            HttpStatus.BAD_REQUEST, 
            String.format("Hesap %s'de yetersiz bakiye. İstenen: %s, Mevcut: %s", accountNumber, requestedAmount, currentBalance),
            "INSUFFICIENT_FUNDS"
        );
    }
    
    public InsufficientFundsException(String message) {
        super(HttpStatus.BAD_REQUEST, message, "INSUFFICIENT_FUNDS");
    }
} 