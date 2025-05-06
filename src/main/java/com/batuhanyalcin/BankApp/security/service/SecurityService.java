package com.batuhanyalcin.BankApp.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.batuhanyalcin.BankApp.repository.AccountRepository;
import com.batuhanyalcin.BankApp.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Mevcut kullanıcının belirli bir ID'ye sahip müşteri olup olmadığını kontrol eder
     * @param customerId müşteri ID'si
     * @return eğer mevcut kullanıcı verilen ID'ye sahipse true, değilse false
     */
    public boolean isCurrentCustomer(Long customerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
                authentication.getPrincipal() instanceof CustomerDetails) {
            CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
            return customerDetails.getId().equals(customerId);
        }
        return false;
    }
    
    /**
     * Mevcut kullanıcının belirli bir e-posta adresine sahip olup olmadığını kontrol eder
     * @param email e-posta adresi
     * @return eğer mevcut kullanıcı verilen e-posta adresine sahipse true, değilse false
     */
    public boolean isCurrentCustomerEmail(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
                authentication.getPrincipal() instanceof CustomerDetails) {
            CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
            return customerDetails.getEmail().equals(email);
        }
        return false;
    }
    
    /**
     * Mevcut kullanıcının belirli bir hesabın sahibi olup olmadığını kontrol eder
     * @param accountId hesap ID'si
     * @return eğer mevcut kullanıcı hesabın sahibiyse true, değilse false
     */
    public boolean isOwnerOfAccount(Long accountId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
                authentication.getPrincipal() instanceof CustomerDetails) {
            CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
            return accountRepository.findById(accountId)
                    .map(account -> account.getCustomer().getId().equals(customerDetails.getId()))
                    .orElse(false);
        }
        return false;
    }
    
    /**
     * Mevcut kullanıcının belirli bir hesap numarasına sahip hesabın sahibi olup olmadığını kontrol eder
     * @param accountNumber hesap numarası
     * @return eğer mevcut kullanıcı hesabın sahibiyse true, değilse false
     */
    public boolean isOwnerOfAccountByNumber(String accountNumber) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
                authentication.getPrincipal() instanceof CustomerDetails) {
            CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
            return accountRepository.findByAccountNumber(accountNumber)
                    .map(account -> account.getCustomer().getId().equals(customerDetails.getId()))
                    .orElse(false);
        }
        return false;
    }
    
    /**
     * Mevcut kullanıcının belirli bir işlemin sahibi olup olmadığını kontrol eder
     * @param transactionId işlem ID'si
     * @return eğer mevcut kullanıcı işlemin sahibiyse true, değilse false
     */
    public boolean isOwnerOfTransaction(Long transactionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
                authentication.getPrincipal() instanceof CustomerDetails) {
            CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
            return transactionRepository.findById(transactionId)
                    .map(transaction -> {
                        boolean isSourceOwner = transaction.getSourceAccount() != null && 
                                transaction.getSourceAccount().getCustomer().getId().equals(customerDetails.getId());
                        boolean isTargetOwner = transaction.getTargetAccount() != null && 
                                transaction.getTargetAccount().getCustomer().getId().equals(customerDetails.getId());
                        return isSourceOwner || isTargetOwner;
                    })
                    .orElse(false);
        }
        return false;
    }
} 