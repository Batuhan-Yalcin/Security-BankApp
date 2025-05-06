package com.batuhanyalcin.BankApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
} 