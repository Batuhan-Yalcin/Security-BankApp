package com.batuhanyalcin.BankApp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.batuhanyalcin.BankApp.entity.Transaction.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    
    private Long id;
    
    private BigDecimal amount;
    
    private LocalDateTime transactionDate;
    
    private TransactionType type;
    
    private String description;
    
    private String sourceAccountNumber;
    
    private String targetAccountNumber;
    
    private String customerName;
} 