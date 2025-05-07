package com.batuhanyalcin.BankApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.dto.JwtResponse;
import com.batuhanyalcin.BankApp.dto.LoginRequest;
import com.batuhanyalcin.BankApp.dto.RegisterRequest;
import com.batuhanyalcin.BankApp.dto.TokenRefreshRequest;
import com.batuhanyalcin.BankApp.dto.TokenRefreshResponse;
import com.batuhanyalcin.BankApp.security.service.AuthService;
import com.batuhanyalcin.BankApp.security.service.CustomerDetails;
import com.batuhanyalcin.BankApp.exception.BadRequestException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Kimlik doğrulama ve yetkilendirme API'leri")
public class AuthController {
    
    private final AuthService authService;
    
    @Operation(summary = "Kullanıcı Kaydı", description = "Yeni bir kullanıcı kaydeder")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }
    
    @Operation(summary = "Kullanıcı Girişi", description = "Kullanıcı girişi yapar ve JWT token döner")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
    
    @Operation(summary = "Token Yenileme", description = "Refresh token ile yeni bir JWT token oluşturur")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
    
    @Operation(summary = "Çıkış Yapma", description = "Kullanıcı oturumunu sonlandırır")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.logout(refreshToken));
    }
    
    @Operation(summary = "Tüm Oturumları Sonlandır", description = "Kullanıcının tüm oturumlarını sonlandırır")
    @PostMapping("/logout-all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> logoutAll(@AuthenticationPrincipal CustomerDetails currentUser) {
        return ResponseEntity.ok(authService.logoutAll(currentUser.getId()));
    }
    
    @Operation(summary = "Kullanıcıya Ait Tüm Tokenları İptal Et", description = "E-posta adresine göre kullanıcıya ait tüm refresh tokenları iptal eder")
    @PostMapping("/revoke-tokens")
    public ResponseEntity<ApiResponse<String>> revokeTokensByEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            throw new BadRequestException("E-posta adresi gereklidir");
        }
        return ResponseEntity.ok(authService.revokeTokensByEmail(email));
    }
    
    @Operation(summary = "Mevcut Kullanıcı", description = "Mevcut oturum açmış kullanıcının bilgilerini döner")
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CustomerDetails> getCurrentUser(@AuthenticationPrincipal CustomerDetails currentUser) {
        return ResponseEntity.ok(currentUser);
    }
    
    @Operation(summary = "Admin Kontrolü", description = "Admin yetkisi kontrolü yapar")
    @GetMapping("/admin-check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> adminCheck() {
        return ResponseEntity.ok(ApiResponse.success("Admin yetkisine sahipsiniz"));
    }
} 