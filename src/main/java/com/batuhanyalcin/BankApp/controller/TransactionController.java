package com.batuhanyalcin.BankApp.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.batuhanyalcin.BankApp.entity.Transaction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.dto.DepositRequest;
import com.batuhanyalcin.BankApp.dto.TransactionDTO;
import com.batuhanyalcin.BankApp.dto.TransferRequest;
import com.batuhanyalcin.BankApp.dto.WithdrawRequest;
import com.batuhanyalcin.BankApp.security.service.SecurityService;
import com.batuhanyalcin.BankApp.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "İşlem Yönetimi", description = "Para işlemleri ve işlem geçmişi API'leri")
public class TransactionController {
    
    private final TransactionService transactionService;
    private final SecurityService securityService;
    
    @Operation(summary = "Tüm İşlemleri Getir", description = "Tüm işlemleri sayfalı olarak getirir")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getAllTransactions(
            @Parameter(description = "Sayfa numarası (0'dan başlar)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa boyutu") 
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.getAllTransactions(page, size));
    }
    
    @Operation(summary = "İşlem Getir", description = "ID'ye göre işlem getirir")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerOfTransaction(#id)")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionById(
            @Parameter(description = "İşlem ID") 
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
    
    @Operation(summary = "Hesap İşlemlerini Getir", description = "Hesap numarasına göre işlemleri getirir")
    @GetMapping("/account/{accountNumber}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerOfAccountByNumber(#accountNumber)")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByAccountNumber(
            @Parameter(description = "Hesap numarası") 
            @PathVariable String accountNumber,
            @Parameter(description = "Sayfa numarası (0'dan başlar)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa boyutu") 
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountNumber(accountNumber, page, size));
    }
    
    @Operation(summary = "Müşteri İşlemlerini Getir", description = "Müşteri ID'sine göre işlemleri getirir")
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentCustomer(#customerId)")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByCustomerId(
            @Parameter(description = "Müşteri ID") 
            @PathVariable Long customerId,
            @Parameter(description = "Sayfa numarası (0'dan başlar)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa boyutu") 
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.getTransactionsByCustomerId(customerId, page, size));
    }
    
    @Operation(summary = "Para Yatır", description = "Hesaba para yatırma işlemi yapar")
    @PostMapping("/deposit")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerOfAccountByNumber(#request.accountNumber)")
    public ResponseEntity<ApiResponse<TransactionDTO>> deposit(
            @Parameter(description = "Para yatırma bilgileri") 
            @Valid @RequestBody DepositRequest request) {
        return new ResponseEntity<>(transactionService.deposit(request), HttpStatus.CREATED);
    }
    
    @Operation(summary = "Para Çek", description = "Hesaptan para çekme işlemi yapar")
    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerOfAccountByNumber(#request.accountNumber)")
    public ResponseEntity<ApiResponse<TransactionDTO>> withdraw(
            @Parameter(description = "Para çekme bilgileri") 
            @Valid @RequestBody WithdrawRequest request) {
        return new ResponseEntity<>(transactionService.withdraw(request), HttpStatus.CREATED);
    }
    
    @Operation(summary = "Para Transferi", description = "Hesaplar arası para transferi yapar")
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerOfAccountByNumber(#request.sourceAccountNumber)")
    public ResponseEntity<ApiResponse<TransactionDTO>> transfer(
            @Parameter(description = "Transfer bilgileri") 
            @Valid @RequestBody TransferRequest request) {
        return new ResponseEntity<>(transactionService.transfer(request), HttpStatus.CREATED);
    }


    /**
     * Belirli bir tarih aralığındaki hesap işlemlerini getiren API endpoint'i
     */
    @Operation(summary = "Tarih Aralığındaki Hesap İşlemlerini Getir", description = "Belirli bir tarih aralığındaki hesap işlemlerini getirir")
    @GetMapping("/account/{accountNumber}/daterange")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerOfAccountByNumber(#accountNumber)")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByAccountNumberAndDateBetween(
            @Parameter(description = "Hesap numarası")
            @PathVariable String accountNumber,
            @Parameter(description = "Başlangıç tarihi (ISO-8601 formatında)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Bitiş tarihi (ISO-8601 formatında)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountNumberAndDateBetween(accountNumber, startDate, endDate));
    }

    /**
     * Belirli bir tarih aralığındaki müşteri işlemlerini getiren API endpoint'i
     */
    @Operation(summary = "Tarih Aralığındaki Müşteri İşlemlerini Getir", description = "Belirli bir tarih aralığındaki müşteri işlemlerini getirir")
    @GetMapping("/customer/{customerId}/daterange")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentCustomer(#customerId)")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByCustomerIdAndDateBetween(
            @Parameter(description = "Müşteri ID")
            @PathVariable Long customerId,
            @Parameter(description = "Başlangıç tarihi (ISO-8601 formatında)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Bitiş tarihi (ISO-8601 formatında)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(transactionService.getTransactionsByCustomerIdAndDateBetween(customerId, startDate, endDate));
    }

    /**
     * Belirli bir tipteki hesap işlemlerini getiren API endpoint'i
     */
    @Operation(summary = "Belirli Tipteki Hesap İşlemlerini Getir", description = "Belirli bir tipteki (para yatırma, çekme, transfer) hesap işlemlerini getirir")
    @GetMapping("/account/{accountNumber}/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerOfAccountByNumber(#accountNumber)")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByAccountNumberAndType(
            @Parameter(description = "Hesap numarası")
            @PathVariable String accountNumber,
            @Parameter(description = "İşlem tipi (DEPOSIT, WITHDRAWAL, TRANSFER)")
            @PathVariable Transaction.TransactionType type) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountNumberAndType(accountNumber, type));
    }
} 