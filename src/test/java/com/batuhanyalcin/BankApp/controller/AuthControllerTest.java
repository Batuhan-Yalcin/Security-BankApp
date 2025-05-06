package com.batuhanyalcin.BankApp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.dto.JwtResponse;
import com.batuhanyalcin.BankApp.dto.LoginRequest;
import com.batuhanyalcin.BankApp.dto.RegisterRequest;
import com.batuhanyalcin.BankApp.dto.TokenRefreshRequest;
import com.batuhanyalcin.BankApp.dto.TokenRefreshResponse;
import com.batuhanyalcin.BankApp.entity.Customer;
import com.batuhanyalcin.BankApp.entity.RefreshToken;
import com.batuhanyalcin.BankApp.entity.Role;
import com.batuhanyalcin.BankApp.security.service.AuthService;
import com.batuhanyalcin.BankApp.security.service.CustomerDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mock;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private SecurityContext securityContext;
    
    @MockBean
    private Authentication authentication;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private TokenRefreshRequest refreshRequest;
    private JwtResponse jwtResponse;
    private TokenRefreshResponse refreshResponse;
    private CustomerDetails customerDetails;
    private Customer customer;
    private Role userRole;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        System.out.println("AuthControllerTest: Test hazırlıkları başlıyor...");

        // Kayıt isteği oluştur
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Batuhan");
        registerRequest.setLastName("Yalçın");
        registerRequest.setEmail("batuhan@yalcin.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhoneNumber("5551234567");
        registerRequest.setAddress("İstanbul, Türkiye");
        
        System.out.println("Test kayıt isteği oluşturuldu: " + registerRequest.getFirstName() + " " + 
                           registerRequest.getLastName());

        // Giriş isteği oluştur
        loginRequest = new LoginRequest();
        loginRequest.setEmail("batuhan@yalcin.com");
        loginRequest.setPassword("password123");
        
        System.out.println("Test giriş isteği oluşturuldu: " + loginRequest.getEmail());

        // Token yenileme isteği oluştur
        refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken("refresh-token");
        
        System.out.println("Test token yenileme isteği oluşturuldu");

        // JWT yanıtı oluştur
        jwtResponse = new JwtResponse();
        jwtResponse.setToken("access-token");
        jwtResponse.setRefreshToken("refresh-token");
        jwtResponse.setType("Bearer");
        jwtResponse.setId(1L);
        jwtResponse.setEmail("batuhan@yalcin.com");
        jwtResponse.setRoles(Arrays.asList("ROLE_USER"));
        
        System.out.println("Test JWT yanıtı oluşturuldu: " + jwtResponse.getEmail());

        // Token yenileme yanıtı oluştur
        refreshResponse = new TokenRefreshResponse();
        refreshResponse.setAccessToken("new-access-token");
        refreshResponse.setRefreshToken("refresh-token");
        refreshResponse.setTokenType("Bearer");
        
        System.out.println("Test token yenileme yanıtı oluşturuldu");

        // User role
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(Role.RoleType.ROLE_USER);
        
        // Customer
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Batuhan");
        customer.setLastName("Yalçın");
        customer.setEmail("batuhan@yalcin.com");
        customer.setPassword("hashedPassword");
        
        // CustomerDetails oluştur
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        customerDetails = new CustomerDetails(1L, "batuhan@yalcin.com", "hashedPassword", 
                                              "Batuhan", "Yalçın", authorities);
        
        System.out.println("Test customer details oluşturuldu: " + customerDetails.getFirstName());
        
        // Security context mock
        Authentication auth = new UsernamePasswordAuthenticationToken(
            customerDetails, null, customerDetails.getAuthorities());
        
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        // Refresh token 
        refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken("refresh-token");
        refreshToken.setCustomer(customer);
        
        System.out.println("AuthControllerTest: Test hazırlıkları tamamlandı.");
    }

    @Test
    @DisplayName("Kullanıcı kaydı testi")
    void register_ShouldReturnSuccess() throws Exception {
        System.out.println("AuthControllerTest: Kullanıcı kaydı testi başlıyor...");
        
        ApiResponse<String> response = ApiResponse.success("Kullanıcı başarıyla kaydedildi");
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Kullanıcı başarıyla kaydedildi"));

        System.out.println("AuthControllerTest: Kullanıcı kaydı testi başarılı!");
    }

    @Test
    @DisplayName("Kullanıcı girişi testi")
    void login_ShouldReturnJwtToken() throws Exception {
        System.out.println("AuthControllerTest: Kullanıcı girişi testi başlıyor...");
        
        ApiResponse<JwtResponse> response = ApiResponse.success("Giriş başarılı", jwtResponse);
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Giriş başarılı"))
                .andExpect(jsonPath("$.data.email").value("batuhan@yalcin.com"))
                .andExpect(jsonPath("$.data.token").value("access-token"));

        System.out.println("AuthControllerTest: Kullanıcı girişi testi başarılı!");
    }

    @Test
    @DisplayName("Token yenileme testi")
    void refreshToken_ShouldReturnNewToken() throws Exception {
        System.out.println("AuthControllerTest: Token yenileme testi başlıyor...");
        
        ApiResponse<TokenRefreshResponse> response = ApiResponse.success("Token başarıyla yenilendi", refreshResponse);
        when(authService.refreshToken(any(TokenRefreshRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token başarıyla yenilendi"))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));

        System.out.println("AuthControllerTest: Token yenileme testi başarılı!");
    }

    @Test
    @DisplayName("Çıkış yapma testi")
    void logout_ShouldReturnSuccess() throws Exception {
        System.out.println("AuthControllerTest: Çıkış yapma testi başlıyor...");
        
        ApiResponse<String> response = ApiResponse.success("Başarıyla çıkış yapıldı");
        when(authService.logout(anyString())).thenReturn(response);

        mockMvc.perform(post("/api/auth/logout")
                .param("refreshToken", "refresh-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Başarıyla çıkış yapıldı"));

        System.out.println("AuthControllerTest: Çıkış yapma testi başarılı!");
    }

    @Test
    @WithMockUser(username = "batuhan@yalcin.com", roles = "USER")
    @DisplayName("Tüm oturumları sonlandırma testi")
    void logoutAll_ShouldReturnSuccess() throws Exception {
        System.out.println("AuthControllerTest: Tüm oturumları sonlandırma testi başlıyor...");
        
        ApiResponse<String> response = ApiResponse.success("Tüm oturumlar başarıyla sonlandırıldı");
        when(authService.logoutAll(anyLong())).thenReturn(response);

        // USER yetkisine sahip yeni bir CustomerDetails oluştur
        Set<GrantedAuthority> userAuthorities = new HashSet<>();
        userAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        CustomerDetails userDetails = new CustomerDetails(1L, "batuhan@yalcin.com", "hashedPassword", 
                                            "Batuhan", "Yalçın", userAuthorities);
        
        // Kullanıcı yetkilendirmesini ekle
        Authentication userAuth = new UsernamePasswordAuthenticationToken(
            userDetails, null, userAuthorities);
        SecurityContextHolder.getContext().setAuthentication(userAuth);

        mockMvc.perform(post("/api/auth/logout-all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tüm oturumlar başarıyla sonlandırıldı"));

        System.out.println("AuthControllerTest: Tüm oturumları sonlandırma testi başarılı!");
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    @DisplayName("Admin yetkisi kontrolü testi")
    void adminCheck_ShouldReturnSuccess() throws Exception {
        System.out.println("AuthControllerTest: Admin yetkisi kontrolü testi başlıyor...");
        
        // Admin yetkisine sahip yeni CustomerDetails nesnesi oluştur
        Set<GrantedAuthority> adminAuthorities = new HashSet<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        CustomerDetails adminDetails = new CustomerDetails(1L, "admin@example.com", "hashedPassword", 
                                              "Admin", "User", adminAuthorities);
        
        // Admin yetkilendirmesini ekle
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
            adminDetails, null, adminAuthorities);
        SecurityContextHolder.getContext().setAuthentication(adminAuth);
        
        ApiResponse<String> response = ApiResponse.success("Admin yetkisine sahipsiniz");
        
        // admin-check endpoint'ine mock istek yapıyoruz
        mockMvc.perform(get("/api/auth/admin-check")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Admin yetkisine sahipsiniz"));

        System.out.println("AuthControllerTest: Admin yetkisi kontrolü testi başarılı!");
    }
} 