package com.batuhanyalcin.BankApp.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequest {
    
    @NotBlank(message = "Hesap numarası boş olamaz")
    private String accountNumber;
    
    @NotNull(message = "Tutar boş olamaz")
    @Positive(message = "Tutar pozitif olmalıdır")
    private BigDecimal amount;
    
    private String description;
} 