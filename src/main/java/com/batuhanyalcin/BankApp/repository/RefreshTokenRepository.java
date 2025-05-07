package com.batuhanyalcin.BankApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.batuhanyalcin.BankApp.entity.Customer;
import com.batuhanyalcin.BankApp.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);
    
    List<RefreshToken> findAllByCustomer(Customer customer);
    
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.customer.id = :customerId")
    void deleteByCustomerId(Long customerId);
    
    @Query("SELECT r FROM RefreshToken r WHERE r.customer.id = :customerId AND r.revoked = false")
    List<RefreshToken> findValidTokensByCustomer(Long customerId);
    
    @Query("SELECT r FROM RefreshToken r WHERE r.customer.id = :customerId AND r.revoked = false")
    List<RefreshToken> findActiveTokensByCustomerId(Long customerId);
} 