package com.batuhanyalcin.BankApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.batuhanyalcin.BankApp.entity.Account;
import com.batuhanyalcin.BankApp.entity.Customer;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByAccountNumber(String accountNumber);
    
    List<Account> findByCustomer(Customer customer);
    
    List<Account> findByCustomerId(Long customerId);
    
    boolean existsByAccountNumber(String accountNumber);
    
    @Query("SELECT a FROM Account a WHERE a.customer.id = :customerId AND a.accountType = :accountType")
    List<Account> findByCustomerIdAndAccountType(Long customerId, Account.AccountType accountType);
    
    @Query("SELECT a FROM Account a WHERE a.customer.email = :email")
    List<Account> findByCustomerEmail(String email);
    
    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.customer.id = :customerId")
    boolean hasAnyAccount(Long customerId);
} 