package com.batuhanyalcin.BankApp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.dto.DepositRequest;
import com.batuhanyalcin.BankApp.dto.TransactionDTO;
import com.batuhanyalcin.BankApp.dto.TransferRequest;
import com.batuhanyalcin.BankApp.dto.WithdrawRequest;
import com.batuhanyalcin.BankApp.entity.Account;
import com.batuhanyalcin.BankApp.entity.Transaction;
import com.batuhanyalcin.BankApp.exception.BadRequestException;
import com.batuhanyalcin.BankApp.exception.BusinessRuleException;
import com.batuhanyalcin.BankApp.exception.InsufficientFundsException;
import com.batuhanyalcin.BankApp.exception.ResourceNotFoundException;
import com.batuhanyalcin.BankApp.repository.AccountRepository;
import com.batuhanyalcin.BankApp.repository.CustomerRepository;
import com.batuhanyalcin.BankApp.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    
    public ApiResponse<List<TransactionDTO>> getAllTransactions(int page, int size) {
        Page<Transaction> transactionPage = transactionRepository.findAll(
                PageRequest.of(page, size, Sort.by("transactionDate").descending())
        );
        
        List<TransactionDTO> transactionDTOs = transactionPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ApiResponse.success("İşlemler başarıyla getirildi", transactionDTOs);
    }
    
    public ApiResponse<TransactionDTO> getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("İşlem", "id", id));
        
        return ApiResponse.success("İşlem başarıyla getirildi", convertToDTO(transaction));
    }
    
    public ApiResponse<List<TransactionDTO>> getTransactionsByAccountNumber(String accountNumber, int page, int size) {
        Page<Transaction> transactions = transactionRepository.findAllByAccountNumber(
                accountNumber, PageRequest.of(page, size, Sort.by("transactionDate").descending())
        );
        
        List<TransactionDTO> transactionDTOs = transactions.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ApiResponse.success("Hesap işlemleri başarıyla getirildi", transactionDTOs);
    }
    
    public ApiResponse<List<TransactionDTO>> getTransactionsByCustomerId(Long customerId, int page, int size) {
        Page<Transaction> transactions = transactionRepository.findAllByCustomerId(
                customerId, PageRequest.of(page, size, Sort.by("transactionDate").descending())
        );
        
        List<TransactionDTO> transactionDTOs = transactions.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ApiResponse.success("Müşteri işlemleri başarıyla getirildi", transactionDTOs);
    }
    
    @Transactional
    public ApiResponse<TransactionDTO> deposit(DepositRequest request) {
        // Hesabı bul
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Hesap", "hesap numarası", request.getAccountNumber()));
        
        // Para yatırma tutarı kontrolü
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Para yatırma tutarı sıfırdan büyük olmalıdır");
        }
        
        // Hesaba para ekle
        account.setBalance(account.getBalance().add(request.getAmount()));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
        
        // İşlem kaydı oluştur
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setDescription(request.getDescription() != null ? 
                request.getDescription() : "Para yatırma işlemi");
        transaction.setTargetAccount(account);
        transaction.setTransactionDate(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return ApiResponse.success("Para yatırma işlemi başarılı", convertToDTO(savedTransaction));
    }
    
    @Transactional
    public ApiResponse<TransactionDTO> withdraw(WithdrawRequest request) {
        // Hesabı bul
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Hesap", "hesap numarası", request.getAccountNumber()));
        
        // Para çekme tutarı kontrolü
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Para çekme tutarı sıfırdan büyük olmalıdır");
        }
        
        // Yeterli bakiye kontrolü
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(
                    account.getAccountNumber(),
                    request.getAmount().toString(),
                    account.getBalance().toString()
            );
        }
        
        // Hesaptan para çıkar
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
        
        // İşlem kaydı oluştur
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.TransactionType.WITHDRAWAL);
        transaction.setDescription(request.getDescription() != null ? 
                request.getDescription() : "Para çekme işlemi");
        transaction.setSourceAccount(account);
        transaction.setTransactionDate(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return ApiResponse.success("Para çekme işlemi başarılı", convertToDTO(savedTransaction));
    }
    
    @Transactional
    public ApiResponse<TransactionDTO> transfer(TransferRequest request) {
        // Kaynak hesabı bul
        Account sourceAccount = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Kaynak hesap", "hesap numarası", request.getSourceAccountNumber()));
        
        // Hedef hesabı bul
        Account targetAccount = accountRepository.findByAccountNumber(request.getTargetAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Hedef hesap", "hesap numarası", request.getTargetAccountNumber()));
        
        // Aynı hesaba transfer kontrolü
        if (sourceAccount.getId().equals(targetAccount.getId())) {
            throw new BusinessRuleException("Aynı hesaba transfer yapılamaz");
        }
        
        // Transfer tutarı kontrolü
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transfer tutarı sıfırdan büyük olmalıdır");
        }
        
        // Yeterli bakiye kontrolü
        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(
                    sourceAccount.getAccountNumber(),
                    request.getAmount().toString(),
                    sourceAccount.getBalance().toString()
            );
        }
        
        // Kaynak hesaptan para çıkar
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        sourceAccount.setUpdatedAt(LocalDateTime.now());
        
        // Hedef hesaba para ekle
        targetAccount.setBalance(targetAccount.getBalance().add(request.getAmount()));
        targetAccount.setUpdatedAt(LocalDateTime.now());
        
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
        
        // İşlem kaydı oluştur
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setDescription(request.getDescription() != null ? 
                request.getDescription() : "Para transferi");
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);
        transaction.setTransactionDate(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return ApiResponse.success("Para transferi başarılı", convertToDTO(savedTransaction));
    }
    
    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setType(transaction.getType());
        dto.setDescription(transaction.getDescription());
        
        if (transaction.getSourceAccount() != null) {
            dto.setSourceAccountNumber(transaction.getSourceAccount().getAccountNumber());
            
            // İşlem türü WITHDRAWAL veya TRANSFER ise müşteri adını kaynaktan al
            if (transaction.getType() == Transaction.TransactionType.WITHDRAWAL || 
                    transaction.getType() == Transaction.TransactionType.TRANSFER) {
                dto.setCustomerName(transaction.getSourceAccount().getCustomer().getFirstName() + " " + 
                        transaction.getSourceAccount().getCustomer().getLastName());
            }
        }
        
        if (transaction.getTargetAccount() != null) {
            dto.setTargetAccountNumber(transaction.getTargetAccount().getAccountNumber());
            
            // İşlem türü DEPOSIT ise müşteri adını hedeften al
            if (transaction.getType() == Transaction.TransactionType.DEPOSIT) {
                dto.setCustomerName(transaction.getTargetAccount().getCustomer().getFirstName() + " " + 
                        transaction.getTargetAccount().getCustomer().getLastName());
            }
        }
        
        return dto;
    }


    /**
     * Belirli bir hesap ID'si ve tarih aralığına göre işlemleri getiren metot
     */
    public ApiResponse<List<TransactionDTO>> getTransactionsByAccountIdAndDateBetween(
            Long accountId, LocalDateTime startDate, LocalDateTime endDate) {

        // Hesabın var olup olmadığını kontrol et
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Hesap", "id", accountId);
        }

        List<Transaction> transactions = transactionRepository.findAllByAccountIdAndDateBetween(
                accountId, startDate, endDate);

        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ApiResponse.success("Belirtilen tarih aralığındaki hesap işlemleri başarıyla getirildi", transactionDTOs);
    }

    /**
     * Belirli bir hesap numarası ve tarih aralığına göre işlemleri getiren metot
     */
    public ApiResponse<List<TransactionDTO>> getTransactionsByAccountNumberAndDateBetween(
            String accountNumber, LocalDateTime startDate, LocalDateTime endDate) {

        // Hesabı bul
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Hesap", "hesap numarası", accountNumber));

        List<Transaction> transactions = transactionRepository.findAllByAccountIdAndDateBetween(
                account.getId(), startDate, endDate);

        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ApiResponse.success("Belirtilen tarih aralığındaki hesap işlemleri başarıyla getirildi", transactionDTOs);
    }

    /**
     * Belirli bir müşteri ID'si ve tarih aralığına göre işlemleri getiren metot
     */
    public ApiResponse<List<TransactionDTO>> getTransactionsByCustomerIdAndDateBetween(
            Long customerId, LocalDateTime startDate, LocalDateTime endDate) {

        // Müşterinin var olup olmadığını kontrol et
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Müşteri", "id", customerId);
        }

        List<Transaction> transactions = transactionRepository.findAllByCustomerIdAndDateBetween(
                customerId, startDate, endDate);

        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ApiResponse.success("Belirtilen tarih aralığındaki müşteri işlemleri başarıyla getirildi", transactionDTOs);
    }

    /**
     * Belirli bir hesap ID'si ve işlem tipine göre işlemleri getiren metot
     */
    public ApiResponse<List<TransactionDTO>> getTransactionsByAccountIdAndType(
            Long accountId, Transaction.TransactionType type) {

        // Hesabın var olup olmadığını kontrol et
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Hesap", "id", accountId);
        }

        List<Transaction> transactions = transactionRepository.findAllByAccountIdAndType(accountId, type);

        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ApiResponse.success("Belirtilen tipteki hesap işlemleri başarıyla getirildi", transactionDTOs);
    }

    /**
     * Belirli bir hesap numarası ve işlem tipine göre işlemleri getiren metot
     */
    public ApiResponse<List<TransactionDTO>> getTransactionsByAccountNumberAndType(
            String accountNumber, Transaction.TransactionType type) {

        // Hesabı bul
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Hesap", "hesap numarası", accountNumber));

        List<Transaction> transactions = transactionRepository.findAllByAccountIdAndType(account.getId(), type);

        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ApiResponse.success("Belirtilen tipteki hesap işlemleri başarıyla getirildi", transactionDTOs);
    }

} 