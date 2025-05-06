package com.batuhanyalcin.BankApp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batuhanyalcin.BankApp.dto.AccountCreateRequest;
import com.batuhanyalcin.BankApp.dto.AccountDTO;
import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.entity.Account;
import com.batuhanyalcin.BankApp.entity.Customer;
import com.batuhanyalcin.BankApp.exception.BadRequestException;
import com.batuhanyalcin.BankApp.exception.DuplicateResourceException;
import com.batuhanyalcin.BankApp.exception.ResourceNotFoundException;
import com.batuhanyalcin.BankApp.repository.AccountRepository;
import com.batuhanyalcin.BankApp.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    
    public ApiResponse<List<AccountDTO>> getAllAccounts(int page, int size) {
        Page<Account> accountPage = accountRepository.findAll(
                PageRequest.of(page, size, Sort.by("id").ascending())
        );
        
        List<AccountDTO> accountDTOs = accountPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ApiResponse.success("Hesaplar başarıyla getirildi", accountDTOs);
    }
    
    public ApiResponse<AccountDTO> getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hesap", "id", id));
        
        return ApiResponse.success("Hesap başarıyla getirildi", convertToDTO(account));
    }
    
    public ApiResponse<AccountDTO> getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Hesap", "hesap numarası", accountNumber));
        
        return ApiResponse.success("Hesap başarıyla getirildi", convertToDTO(account));
    }
    
    public ApiResponse<List<AccountDTO>> getAccountsByCustomerId(Long customerId) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        
        List<AccountDTO> accountDTOs = accounts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ApiResponse.success("Müşteri hesapları başarıyla getirildi", accountDTOs);
    }
    
    @Transactional
    public ApiResponse<AccountDTO> createAccount(AccountCreateRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri", "id", request.getCustomerId()));
        
        Account account = new Account();
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO);
        account.setCustomer(customer);
        
        // Hesap numarası oluştur
        String accountNumber = generateUniqueAccountNumber();
        account.setAccountNumber(accountNumber);
        
        // Oluşturma tarihi
        LocalDateTime now = LocalDateTime.now();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        
        Account savedAccount = accountRepository.save(account);
        
        return ApiResponse.success("Hesap başarıyla oluşturuldu", convertToDTO(savedAccount));
    }
    
    @Transactional
    public ApiResponse<AccountDTO> updateAccount(Long id, AccountDTO accountDTO) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hesap", "id", id));
        
        // Hesap numarası değiştirilmek isteniyorsa ve başka bir hesapta varsa hata fırlat
        if (!existingAccount.getAccountNumber().equals(accountDTO.getAccountNumber()) && 
                accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
            throw new DuplicateResourceException("Hesap", "hesap numarası", accountDTO.getAccountNumber());
        }
        
        existingAccount.setAccountType(accountDTO.getAccountType());
        existingAccount.setUpdatedAt(LocalDateTime.now());
        
        Account updatedAccount = accountRepository.save(existingAccount);
        
        return ApiResponse.success("Hesap başarıyla güncellendi", convertToDTO(updatedAccount));
    }
    
    @Transactional
    public ApiResponse<String> deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hesap", "id", id));
        
        // Hesapta para varsa silme işlemi yapılmamalı
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Bakiyesi olan hesap silinemez. Lütfen önce bakiyeyi sıfırlayın.");
        }
        
        accountRepository.delete(account);
        
        return ApiResponse.success("Hesap başarıyla silindi");
    }
    
    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            // Rastgele hesap numarası oluştur (10 karakter)
            accountNumber = "TR" + UUID.randomUUID().toString()
                    .replaceAll("-", "")
                    .substring(0, 10)
                    .toUpperCase();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        
        return accountNumber;
    }
    
    private AccountDTO convertToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setBalance(account.getBalance());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        dto.setCustomerId(account.getCustomer().getId());
        dto.setCustomerName(account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName());
        
        return dto;
    }
} 