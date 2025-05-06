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
        // E-posta kontrolü
        if (customerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Müşteri", "email", registerRequest.getEmail());
        }
        
        // Yeni müşteri oluşturma
        Customer customer = new Customer();
        customer.setFirstName(registerRequest.getFirstName());
        customer.setLastName(registerRequest.getLastName());
        customer.setEmail(registerRequest.getEmail());
        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        customer.setPhoneNumber(registerRequest.getPhoneNumber());
        customer.setAddress(registerRequest.getAddress());
        
        // Roller atama
        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        
        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(Role.RoleType.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol", "name", "ROLE_USER"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toUpperCase()) {
                    case "ADMIN":
                        Role adminRole = roleRepository.findByName(Role.RoleType.ROLE_ADMIN)
                                .orElseThrow(() -> new ResourceNotFoundException("Rol", "name", "ROLE_ADMIN"));
                        roles.add(adminRole);
                        break;
                    case "USER":
                    default:
                        Role userRole = roleRepository.findByName(Role.RoleType.ROLE_USER)
                                .orElseThrow(() -> new ResourceNotFoundException("Rol", "name", "ROLE_USER"));
                        roles.add(userRole);
                }
            });
        }
        
        customer.setRoles(roles);
        customerRepository.save(customer);
        
        return ApiResponse.success("Kullanıcı başarıyla kaydedildi");
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
    
    public CustomerDetails getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
                !(authentication.getPrincipal() instanceof CustomerDetails)) {
            throw new UnauthorizedException("Kimlik doğrulama gerekli");
        }
        
        return (CustomerDetails) authentication.getPrincipal();
    }
} 