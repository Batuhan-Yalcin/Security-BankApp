package com.batuhanyalcin.BankApp.security.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.dto.JwtResponse;
import com.batuhanyalcin.BankApp.dto.LoginRequest;
import com.batuhanyalcin.BankApp.dto.RegisterRequest;
import com.batuhanyalcin.BankApp.dto.TokenRefreshRequest;
import com.batuhanyalcin.BankApp.dto.TokenRefreshResponse;
import com.batuhanyalcin.BankApp.entity.Customer;
import com.batuhanyalcin.BankApp.entity.RefreshToken;
import com.batuhanyalcin.BankApp.entity.Role;
import com.batuhanyalcin.BankApp.exception.BadRequestException;
import com.batuhanyalcin.BankApp.exception.DuplicateResourceException;
import com.batuhanyalcin.BankApp.exception.ResourceNotFoundException;
import com.batuhanyalcin.BankApp.exception.UnauthorizedException;
import com.batuhanyalcin.BankApp.repository.CustomerRepository;
import com.batuhanyalcin.BankApp.repository.RoleRepository;
import com.batuhanyalcin.BankApp.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    
    @Transactional
    public ApiResponse<String> register(RegisterRequest registerRequest) {
        try {
            // E-posta kontrolü
            if (customerRepository.existsByEmail(registerRequest.getEmail())) {
                throw new DuplicateResourceException("Müşteri", "email", registerRequest.getEmail());
            }
            
            // Önce USER rolünü veritabanından al
            Role userRole = roleRepository.findByName(Role.RoleType.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol", "name", "ROLE_USER"));
            
            // Yeni müşteri oluşturma
            Customer customer = new Customer();
            customer.setFirstName(registerRequest.getFirstName());
            customer.setLastName(registerRequest.getLastName());
            customer.setEmail(registerRequest.getEmail());
            customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            customer.setPhoneNumber(registerRequest.getPhoneNumber());
            customer.setAddress(registerRequest.getAddress());
            
            // Yeni müşteri için HashSet oluştur
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            customer.setRoles(roles);
            
            // Kaydedip sonucu döndür
            customerRepository.save(customer);
            
            return ApiResponse.success("Kullanıcı başarıyla kaydedildi");
        } catch (Exception e) {
            // Tüm hataları yakala ve loglama yap
            e.printStackTrace();
            throw e;
        }
    }
    
    @Transactional
    public ApiResponse<JwtResponse> login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
            
            String jwt = jwtService.generateToken(customerDetails);
            
            // Giriş yapmadan önce mevcut tüm refresh token'ları iptal et
            try {
                refreshTokenService.revokeAllCustomerTokens(customerDetails.getId());
            } catch (Exception e) {
          
                System.err.println("Token iptal hatası (önemli değil): " + e.getMessage());
            }
            
            // Yeni refresh token oluştur
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(customerDetails.getId());
            
            List<String> roles = customerDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            
            JwtResponse jwtResponse = new JwtResponse(
                    jwt,
                    refreshToken.getToken(),
                    "Bearer",
                    customerDetails.getId(),
                    customerDetails.getEmail(),
                    customerDetails.getFirstName(),
                    customerDetails.getLastName(),
                    roles
            );
            
            return ApiResponse.success("Giriş başarılı", jwtResponse);
            
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Geçersiz kullanıcı adı veya şifre");
        } catch (Exception e) {
            // Hata detaylarını log et
            System.err.println("Login sırasında beklenmeyen hata: " + e.getMessage());
            e.printStackTrace();
            
            // Nedenini kontrol et ve uygun mesaj döndür
            if (e.getMessage() != null && e.getMessage().contains("duplicate key")) {
                throw new BadRequestException("Oturum açma hatası: Token çakışması. Lütfen tekrar deneyiniz.");
            }
            
            // Diğer hatalar için genel mesaj
            throw new BadRequestException("Giriş sırasında beklenmeyen bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.");
        }
    }
    
    @Transactional
    public ApiResponse<TokenRefreshResponse> refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshToken -> {
                    RefreshToken verifiedToken = refreshTokenService.verifyExpiration(requestRefreshToken);
                    Customer customer = verifiedToken.getCustomer();
                    CustomerDetails customerDetails = CustomerDetails.build(customer);
                    
                    String token = jwtService.generateToken(customerDetails);
                    
                    return ApiResponse.success(
                            "Token yenilendi",
                            new TokenRefreshResponse(token, requestRefreshToken, "Bearer")
                    );
                })
                .orElseThrow(() -> new BadRequestException("Refresh token geçersiz"));
    }
    
    @Transactional
    public ApiResponse<String> logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenService.revokeToken(refreshToken);
        }
        
        SecurityContextHolder.clearContext();
        
        return ApiResponse.success("Çıkış başarılı");
    }
    
    @Transactional
    public ApiResponse<String> logoutAll(Long customerId) {
        refreshTokenService.revokeAllCustomerTokens(customerId);
        SecurityContextHolder.clearContext();
        
        return ApiResponse.success("Tüm oturumlar sonlandırıldı");
    }
    
    @Transactional
    public ApiResponse<String> revokeTokensByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri", "email", email));
        
        refreshTokenService.revokeAllCustomerTokens(customer.getId());
        
        return ApiResponse.success("Kullanıcıya ait tüm tokenlar iptal edildi");
    }
    
    public CustomerDetails getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
                !(authentication.getPrincipal() instanceof CustomerDetails)) {
            throw new UnauthorizedException("Kimlik doğrulama gerekli");
        }
        
        return (CustomerDetails) authentication.getPrincipal();
    }
} 