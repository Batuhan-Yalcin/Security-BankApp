package com.batuhanyalcin.BankApp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.batuhanyalcin.BankApp.dto.AccountCreateRequest;
import com.batuhanyalcin.BankApp.dto.AccountDTO;
import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.security.service.CustomerDetails;
import com.batuhanyalcin.BankApp.security.service.SecurityService;
import com.batuhanyalcin.BankApp.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Hesap Yönetimi", description = "Hesap CRUD işlemleri")
public class AccountController {
    
    private final AccountService accountService;
    private final SecurityService securityService;
    
    @Operation(summary = "Tüm Hesapları Getir", description = "Tüm hesapları sayfalı olarak getirir")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AccountDTO>>> getAllAccounts(
            @Parameter(description = "Sayfa numarası (0'dan başlar)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa boyutu")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(accountService.getAllAccounts(page, size));
    }
    
    @Operation(summary = "Hesap Getir", description = "ID'ye göre hesap getirir")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerOfAccount(#id)")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountById(
            @Parameter(description = "Hesap ID")
            @PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }
    
    @Operation(summary = "Hesap Numarasına Göre Hesap Getir", description = "Hesap numarasına göre hesap getirir")
    @GetMapping("/number/{accountNumber}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerOfAccountByNumber(#accountNumber)")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountByAccountNumber(
            @Parameter(description = "Hesap numarası")
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByAccountNumber(accountNumber));
    }
    
    @Operation(summary = "Müşteri Hesaplarını Getir", description = "Müşteri ID'sine göre hesapları getirir")
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentCustomer(#customerId)")
    public ResponseEntity<ApiResponse<List<AccountDTO>>> getAccountsByCustomerId(
            @Parameter(description = "Müşteri ID")
            @PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getAccountsByCustomerId(customerId));
    }
    
    @Operation(summary = "Hesap Oluştur", description = "Yeni bir hesap oluşturur")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentCustomer(#request.customerId)")
    public ResponseEntity<ApiResponse<AccountDTO>> createAccount(
            @Parameter(description = "Hesap oluşturma bilgileri")
            @Valid @RequestBody AccountCreateRequest request) {
        return new ResponseEntity<>(accountService.createAccount(request), HttpStatus.CREATED);
    }
    
    @Operation(summary = "Hesap Güncelle", description = "Mevcut bir hesabı günceller")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AccountDTO>> updateAccount(
            @Parameter(description = "Hesap ID")
            @PathVariable Long id,
            @Parameter(description = "Güncellenmiş hesap bilgileri")
            @Valid @RequestBody AccountDTO accountDTO) {
        return ResponseEntity.ok(accountService.updateAccount(id, accountDTO));
    }
    
    @Operation(summary = "Hesap Sil", description = "ID'ye göre hesap siler (sadece bakiyesi sıfır olan hesaplar için)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteAccount(
            @Parameter(description = "Hesap ID")
            @PathVariable Long id) {
        return ResponseEntity.ok(accountService.deleteAccount(id));
    }
    
    @Operation(summary = "Toplam Bakiye Bilgisi", description = "Kullanıcının tüm hesaplarının toplam bakiye bilgisini döndürür")
    @GetMapping("/balance")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAccountBalance(
            @AuthenticationPrincipal CustomerDetails currentUser) {
        // Bu endpoint şu an için test amaçlı mock veri dönüyor
        // Gerçek uygulamada kullanıcının hesaplarından alınacak
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> accounts = new ArrayList<>();
        
        Map<String, Object> account1 = new HashMap<>();
        account1.put("id", 1);
        account1.put("name", "Ana Hesap");
        account1.put("balance", 28540.50);
        account1.put("currency", "TL");
        accounts.add(account1);
        
        Map<String, Object> account2 = new HashMap<>();
        account2.put("id", 2);
        account2.put("name", "Tasarruf Hesabı");
        account2.put("balance", 14210.35);
        account2.put("currency", "TL");
        accounts.add(account2);
        
        response.put("total", 42750.85);
        response.put("change", 2.4);
        response.put("accounts", accounts);
        
        return ResponseEntity.ok(ApiResponse.success("Bakiye bilgisi başarıyla getirildi", response));
    }
} 