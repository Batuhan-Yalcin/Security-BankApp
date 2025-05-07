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
import com.batuhanyalcin.BankApp.exception.ResourceNotFoundException;
import com.batuhanyalcin.BankApp.exception.TokenRefreshException;
import com.batuhanyalcin.BankApp.repository.CustomerRepository;
import com.batuhanyalcin.BankApp.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomerRepository customerRepository;
    
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDuration;
    
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    
    @Transactional
    public RefreshToken createRefreshToken(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri", "id", customerId));
        
        // Önce kullanıcıya ait tüm refresh token'ları silmeyi dene
        try {
            // Mevcut tüm tokenları sil (hem revoke et hem de veritabanından kaldır)
            List<RefreshToken> existingTokens = refreshTokenRepository.findAllByCustomer(customer);
            if (!existingTokens.isEmpty()) {
                System.out.println("Kullanıcı ID: " + customerId + " için " + existingTokens.size() + " adet token siliniyor");
                refreshTokenRepository.deleteAll(existingTokens);
                refreshTokenRepository.flush(); // Veritabanı işlemlerini hemen uygula
            }
        } catch (Exception e) {
            System.err.println("Mevcut tokenları silme hatası (devam edilecek): " + e.getMessage());
            e.printStackTrace();
        }
        
        // Yeni token oluştur
        RefreshToken refreshToken = RefreshToken.builder()
                .customer(customer)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(refreshTokenDuration))
                .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    @Transactional
    public RefreshToken verifyExpiration(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(refreshToken -> {
                    if (refreshToken.isRevoked()) {
                        refreshTokenRepository.delete(refreshToken);
                        throw new TokenRefreshException(token, "Refresh token revoked. Please signin again");
                    }
                    
                    if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
                        refreshTokenRepository.delete(refreshToken);
                        throw new TokenRefreshException(token, "Refresh token was expired. Please signin again");
                    }
                    
                    return refreshToken;
                })
                .orElseThrow(() -> new TokenRefreshException(token, "Refresh token not found in database"));
    }
    
    @Transactional
    public int revokeAllCustomerTokens(Long customerId) {
        try {
            System.out.println("Tüm tokenlar iptal ediliyor, müşteri ID: " + customerId);
            
            // Önce veritabanında var olan tokenları bulalım
            List<RefreshToken> tokensToRevoke = refreshTokenRepository.findActiveTokensByCustomerId(customerId);
            
            if (tokensToRevoke.isEmpty()) {
                System.out.println("İptal edilecek token bulunmamaktadır.");
                return 0;
            }
            
            System.out.println("İptal edilecek token sayısı: " + tokensToRevoke.size());
            
            // Tüm tokenları iptal et
            for (RefreshToken token : tokensToRevoke) {
                token.setRevoked(true);
            }
            refreshTokenRepository.saveAll(tokensToRevoke);
            
            // Veritabanı işlemlerinin hemen uygulanmasını sağla
            refreshTokenRepository.flush();
            
            // Temizlik: Ardından tüm iptal edilmiş tokenları sil (opsiyonel)
            try {
                refreshTokenRepository.deleteByCustomerId(customerId);
            } catch (Exception e) {
                System.err.println("Tüm tokenları silme işlemi başarısız: " + e.getMessage());
                // Hata silme işleminde olduysa da revoke işlemi başarılıdır
            }
            
            return tokensToRevoke.size();
        } catch (Exception e) {
            System.err.println("Token iptal işlemi hatası: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    @Transactional
    public void revokeToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException(token, "Refresh token not found in database"));
        
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
} 