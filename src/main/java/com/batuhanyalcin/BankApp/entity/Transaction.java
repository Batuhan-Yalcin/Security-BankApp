package com.batuhanyalcin.BankApp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "İşlem tutarı boş olamaz")
    @Positive(message = "İşlem tutarı pozitif olmalıdır")
    @Column(nullable = false)
    private BigDecimal amount;
    
    @NotNull(message = "İşlem tarihi boş olamaz")
    @Column(nullable = false)
    private LocalDateTime transactionDate;
    
    @NotNull(message = "İşlem türü belirtilmelidir")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    @Size(max = 255, message = "Açıklama çok uzun")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;
    
    @ManyToOne
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;
    
    @PrePersist
    protected void onCreate() {
        this.transactionDate = LocalDateTime.now();
    }
    
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER
    }
} 