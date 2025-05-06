package com.batuhanyalcin.BankApp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.batuhanyalcin.BankApp.entity.Account.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    
    private Long id;
    
    @NotBlank(message = "Hesap numarası boş olamaz")
    @Pattern(regexp = "^[A-Z0-9]{10,16}$", message = "Hesap numarası 10-16 karakter arasında olmalı ve sadece büyük harf ve rakam içermelidir")
    private String accountNumber;
    
    @NotNull(message = "Hesap türü belirtilmelidir")
    private AccountType accountType;
    
    @NotNull(message = "Bakiye null olamaz")
    @PositiveOrZero(message = "Bakiye negatif olamaz")
    private BigDecimal balance;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Long customerId;
    
    private String customerName;
} 