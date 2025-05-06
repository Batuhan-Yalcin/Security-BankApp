package com.batuhanyalcin.BankApp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Hesap numarası boş olamaz")
    @Pattern(regexp = "^[A-Z0-9]{10,16}$", message = "Hesap numarası 10-16 karakter arasında olmalı ve sadece büyük harf ve rakam içermelidir")
    @Column(nullable = false, unique = true)
    private String accountNumber;
    
    @NotNull(message = "Hesap türü belirtilmelidir")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;
    
    @NotNull(message = "Bakiye null olamaz")
    @PositiveOrZero(message = "Bakiye negatif olamaz")
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @NotNull(message = "Oluşturma tarihi boş olamaz")
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @NotNull(message = "Müşteri bilgisi boş olamaz")
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL)
    private Set<Transaction> outgoingTransactions = new HashSet<>();
    
    @OneToMany(mappedBy = "targetAccount", cascade = CascadeType.ALL)
    private Set<Transaction> incomingTransactions = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum AccountType {
        CHECKING,
        SAVINGS,
        CREDIT
    }
} 