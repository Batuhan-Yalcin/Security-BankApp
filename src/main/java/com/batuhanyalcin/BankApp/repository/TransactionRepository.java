package com.batuhanyalcin.BankApp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.batuhanyalcin.BankApp.entity.Account;
import com.batuhanyalcin.BankApp.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount.id = :accountId OR t.targetAccount.id = :accountId ORDER BY t.transactionDate DESC")
    Page<Transaction> findAllByAccountId(Long accountId, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount.accountNumber = :accountNumber OR t.targetAccount.accountNumber = :accountNumber ORDER BY t.transactionDate DESC")
    Page<Transaction> findAllByAccountNumber(String accountNumber, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount.customer.id = :customerId OR t.targetAccount.customer.id = :customerId ORDER BY t.transactionDate DESC")
    Page<Transaction> findAllByCustomerId(Long customerId, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE (t.sourceAccount.id = :accountId OR t.targetAccount.id = :accountId) AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transaction> findAllByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE (t.sourceAccount.customer.id = :customerId OR t.targetAccount.customer.id = :customerId) AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transaction> findAllByCustomerIdAndDateBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.type = :type AND (t.sourceAccount.id = :accountId OR t.targetAccount.id = :accountId) ORDER BY t.transactionDate DESC")
    List<Transaction> findAllByAccountIdAndType(Long accountId, Transaction.TransactionType type);
    
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount = :account OR t.targetAccount = :account ORDER BY t.transactionDate DESC")
    List<Transaction> findAllByAccount(Account account);
} 