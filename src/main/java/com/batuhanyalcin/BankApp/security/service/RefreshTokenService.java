package com.batuhanyalcin.BankApp.security.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batuhanyalcin.BankApp.entity.Customer;
import com.batuhanyalcin.BankApp.entity.RefreshToken;
import com.batuhanyalcin.BankApp.exception.BadRequestException;
import com.batuhanyalcin.BankApp.exception.ResourceNotFoundException;
import com.batuhanyalcin.BankApp.repository.CustomerRepository;
import com.batuhanyalcin.BankApp.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomerRepository customerRepository;
    
    @Value("${jwt.refresh.expiration.days:7}")
    private long refreshTokenDurationDays;
    
    @Transactional
    public RefreshToken createRefreshToken(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri", "id", customerId));
        
        RefreshToken refreshToken = RefreshToken.builder()
                .customer(customer)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(refreshTokenDurationDays * 24 * 60 * 60))
                .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    @Transactional
    public RefreshToken verifyExpiration(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Geçersiz refresh token"));
        
        if (refreshToken.isExpired() || refreshToken.isRevoked()) {
            refreshTokenRepository.delete(refreshToken);
            throw new BadRequestException("Refresh token süresi dolmuş veya geçersiz. Lütfen tekrar giriş yapın.");
        }
        
        return refreshToken;
    }
    
    @Transactional
    public void deleteByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri", "id", customerId));
        
        refreshTokenRepository.deleteByCustomer(customer);
    }
    
    @Transactional
    public void revokeToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Geçersiz refresh token"));
        
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
    
    @Transactional
    public void revokeAllCustomerTokens(Long customerId) {
        List<RefreshToken> validTokens = refreshTokenRepository.findValidTokensByCustomer(customerId);
        
        if (!validTokens.isEmpty()) {
            validTokens.forEach(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }
    }
    
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
} 