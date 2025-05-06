package com.batuhanyalcin.BankApp.dto;

import java.math.BigDecimal;

import com.batuhanyalcin.BankApp.entity.Account.AccountType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateRequest {
    
    @NotNull(message = "Hesap türü belirtilmelidir")
    private AccountType accountType;
    
    @PositiveOrZero(message = "Başlangıç bakiyesi negatif olamaz")
    private BigDecimal initialBalance = BigDecimal.ZERO;
    
    @NotNull(message = "Müşteri ID boş olamaz")
    private Long customerId;
} 