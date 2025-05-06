package com.batuhanyalcin.BankApp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.dto.CustomerDTO;
import com.batuhanyalcin.BankApp.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Müşteri Yönetimi", description = "Müşteri CRUD işlemleri")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @Operation(summary = "Tüm Müşterileri Getir", description = "Tüm müşterileri sayfalı olarak getirir")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAllCustomers(
            @Parameter(description = "Sayfa numarası (0'dan başlar)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa boyutu")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(customerService.getAllCustomers(page, size));
    }
    
    @Operation(summary = "Müşteri Getir", description = "ID'ye göre müşteri getirir")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentCustomer(#id)")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerById(
            @Parameter(description = "Müşteri ID")
            @PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }
    
    @Operation(summary = "Email ile Müşteri Getir", description = "Email adresine göre müşteri getirir")
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentCustomerEmail(#email)")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerByEmail(
            @Parameter(description = "Müşteri email adresi")
            @PathVariable String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }
    
    @Operation(summary = "Müşteri Oluştur", description = "Yeni bir müşteri oluşturur")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CustomerDTO>> createCustomer(
            @Parameter(description = "Müşteri bilgileri")
            @Valid @RequestBody CustomerDTO customerDTO) {
        return new ResponseEntity<>(customerService.createCustomer(customerDTO), HttpStatus.CREATED);
    }
    
    @Operation(summary = "Müşteri Güncelle", description = "Mevcut bir müşteriyi günceller")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentCustomer(#id)")
    public ResponseEntity<ApiResponse<CustomerDTO>> updateCustomer(
            @Parameter(description = "Müşteri ID")
            @PathVariable Long id,
            @Parameter(description = "Güncellenmiş müşteri bilgileri")
            @Valid @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDTO));
    }
    
    @Operation(summary = "Müşteri Sil", description = "ID'ye göre müşteri siler")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(
            @Parameter(description = "Müşteri ID")
            @PathVariable Long id) {
        return ResponseEntity.ok(customerService.deleteCustomer(id));
    }
} 