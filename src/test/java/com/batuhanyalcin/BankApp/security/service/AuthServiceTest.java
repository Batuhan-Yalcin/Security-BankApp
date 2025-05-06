package com.batuhanyalcin.BankApp.security.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.dto.JwtResponse;
import com.batuhanyalcin.BankApp.dto.LoginRequest;
import com.batuhanyalcin.BankApp.dto.RegisterRequest;
import com.batuhanyalcin.BankApp.dto.TokenRefreshRequest;
import com.batuhanyalcin.BankApp.dto.TokenRefreshResponse;
import com.batuhanyalcin.BankApp.entity.Customer;
import com.batuhanyalcin.BankApp.entity.RefreshToken;
import com.batuhanyalcin.BankApp.entity.Role;
import com.batuhanyalcin.BankApp.repository.CustomerRepository;
import com.batuhanyalcin.BankApp.repository.RoleRepository;
import com.batuhanyalcin.BankApp.security.jwt.JwtService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private Customer customer;
    private Role userRole;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private TokenRefreshRequest refreshRequest;
    private RefreshToken refreshToken;
    private CustomerDetails customerDetails;

    @BeforeEach
    void setUp() {
        System.out.println("AuthServiceTest: Test hazırlıkları başlıyor...");
        
        // Test için rol oluştur
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(Role.RoleType.ROLE_USER);
        
        // Test için müşteri entity'si oluştur
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Batuhan");
        customer.setLastName("Yalçın");
        customer.setEmail("batuhan@yalcin.com");
        customer.setPassword("hashedPassword");
        customer.setPhoneNumber("5551234567");
        customer.setAddress("İstanbul, Türkiye");
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        customer.setRoles(roles);
        
        System.out.println("Test müşterisi oluşturuldu: " + customer.getFirstName() + " " + customer.getLastName());

        // Test için kayıt isteği oluştur
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Batuhan");
        registerRequest.setLastName("Yalçın");
        registerRequest.setEmail("batuhan@yalcin.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhoneNumber("5551234567");
        registerRequest.setAddress("İstanbul, Türkiye");
        
        System.out.println("Test kayıt isteği oluşturuldu");

        // Test için giriş isteği oluştur
        loginRequest = new LoginRequest();
        loginRequest.setEmail("batuhan@yalcin.com");
        loginRequest.setPassword("password123");
        
        System.out.println("Test giriş isteği oluşturuldu");
        
        // Test için CustomerDetails oluştur
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        customerDetails = new CustomerDetails(
            customer.getId(),
            customer.getEmail(),
            customer.getPassword(),
            customer.getFirstName(),
            customer.getLastName(),
            authorities
        );
        
        System.out.println("Test CustomerDetails oluşturuldu");
        
        // Test için refresh token oluştur
        refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setCustomer(customer);
        refreshToken.setToken("refresh-token-123");
        refreshToken.setExpiryDate(java.time.Instant.now().plusSeconds(600));
        
        System.out.println("Test refresh token oluşturuldu");
        
        // Test için token refresh isteği oluştur
        refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken("refresh-token-123");
        
        System.out.println("Test token refresh isteği oluşturuldu");
        
        System.out.println("AuthServiceTest: Test hazırlıkları tamamlandı.");
    }

    @Test
    @DisplayName("Kullanıcı kayıt testi")
    void register_ShouldReturnSuccess() {
        System.out.println("AuthServiceTest: Kullanıcı kayıt testi başlıyor...");
        
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(Role.RoleType.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        ApiResponse<String> response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Kullanıcı başarıyla kaydedildi", response.getMessage());
        
        verify(customerRepository, times(1)).existsByEmail(anyString());
        verify(roleRepository, times(1)).findByName(Role.RoleType.ROLE_USER);
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(customerRepository, times(1)).save(any(Customer.class));
        
        System.out.println("Kullanıcı başarıyla kaydedildi");
        System.out.println("AuthServiceTest: Kullanıcı kayıt testi başarılı!");
    }

    @Test
    @DisplayName("Kullanıcı giriş testi")
    void login_ShouldReturnJwtResponse() {
        System.out.println("AuthServiceTest: Kullanıcı giriş testi başlıyor...");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customerDetails);
        when(jwtService.generateToken(any(CustomerDetails.class))).thenReturn("jwt-token-123");
        when(refreshTokenService.createRefreshToken(anyLong())).thenReturn(refreshToken);

        ApiResponse<JwtResponse> response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Giriş başarılı", response.getMessage());
        assertNotNull(response.getData());
        
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(any(CustomerDetails.class));
        verify(refreshTokenService, times(1)).createRefreshToken(anyLong());
        
        System.out.println("Kullanıcı başarıyla giriş yaptı: " + response.getData().getEmail());
        System.out.println("Token: " + response.getData().getToken());
        System.out.println("AuthServiceTest: Kullanıcı giriş testi başarılı!");
    }

    @Test
    @DisplayName("Token yenileme testi")
    void refreshToken_ShouldReturnNewToken() {
        System.out.println("AuthServiceTest: Token yenileme testi başlıyor...");
        
        when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(anyString())).thenReturn(refreshToken);
        when(jwtService.generateToken(any(CustomerDetails.class))).thenReturn("new-jwt-token-123");

        ApiResponse<TokenRefreshResponse> response = authService.refreshToken(refreshRequest);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Token yenilendi", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("new-jwt-token-123", response.getData().getAccessToken());
        
        verify(refreshTokenService, times(1)).findByToken(anyString());
        verify(refreshTokenService, times(1)).verifyExpiration(anyString());
        verify(jwtService, times(1)).generateToken(any(CustomerDetails.class));
        
        System.out.println("Token başarıyla yenilendi: " + response.getData().getAccessToken());
        System.out.println("AuthServiceTest: Token yenileme testi başarılı!");
    }

    @Test
    @DisplayName("Kullanıcı çıkış testi")
    void logout_ShouldReturnSuccess() {
        System.out.println("AuthServiceTest: Kullanıcı çıkış testi başlıyor...");

        ApiResponse<String> response = authService.logout("refresh-token-123");

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Çıkış başarılı", response.getMessage());
        
        verify(refreshTokenService, times(1)).revokeToken(anyString());
        
        System.out.println("Kullanıcı başarıyla çıkış yaptı");
        System.out.println("AuthServiceTest: Kullanıcı çıkış testi başarılı!");
    }

    @Test
    @DisplayName("Tüm oturumları sonlandırma testi")
    void logoutAll_ShouldReturnSuccess() {
        System.out.println("AuthServiceTest: Tüm oturumları sonlandırma testi başlıyor...");

        ApiResponse<String> response = authService.logoutAll(1L);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Tüm oturumlar sonlandırıldı", response.getMessage());
        
        verify(refreshTokenService, times(1)).revokeAllCustomerTokens(anyLong());
        
        System.out.println("Tüm oturumlar başarıyla sonlandırıldı");
        System.out.println("AuthServiceTest: Tüm oturumları sonlandırma testi başarılı!");
    }
} 