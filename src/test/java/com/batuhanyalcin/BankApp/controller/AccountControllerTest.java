package com.batuhanyalcin.BankApp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.batuhanyalcin.BankApp.dto.AccountCreateRequest;
import com.batuhanyalcin.BankApp.dto.AccountDTO;
import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.entity.Account;
import com.batuhanyalcin.BankApp.security.service.SecurityService;
import com.batuhanyalcin.BankApp.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private SecurityService securityService;

    private AccountDTO accountDTO;
    private AccountCreateRequest createRequest;
    private ApiResponse<List<AccountDTO>> listResponse;
    private ApiResponse<AccountDTO> singleResponse;

    @BeforeEach
    void setUp() {
        System.out.println("AccountControllerTest: Test hazırlıkları başlıyor...");

        // Test için hesap DTO'su oluştur
        accountDTO = new AccountDTO();
        accountDTO.setId(1L);
        accountDTO.setAccountNumber("TR1234567890");
        accountDTO.setAccountType(Account.AccountType.CHECKING);
        accountDTO.setBalance(new BigDecimal("5000.00"));
        accountDTO.setCustomerId(1L);
        accountDTO.setCustomerName("Batuhan Yalçın");
        accountDTO.setCreatedAt(LocalDateTime.now());
        accountDTO.setUpdatedAt(LocalDateTime.now());

        System.out.println("Test hesabı oluşturuldu: " + accountDTO.getAccountNumber() + 
                           " (Müşteri: " + accountDTO.getCustomerName() + ")");

        // Test için hesap oluşturma isteği oluştur
        createRequest = new AccountCreateRequest();
        createRequest.setCustomerId(1L);
        createRequest.setAccountType(Account.AccountType.CHECKING);
        createRequest.setInitialBalance(new BigDecimal("1000.00"));

        System.out.println("Test hesap oluşturma isteği oluşturuldu.");

        // Mock yanıtları hazırla
        listResponse = ApiResponse.success("Hesaplar başarıyla getirildi", Arrays.asList(accountDTO));
        singleResponse = ApiResponse.success("Hesap başarıyla getirildi", accountDTO);

        System.out.println("AccountControllerTest: Test hazırlıkları tamamlandı.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Tüm hesapları getirme testi")
    void getAllAccounts_ShouldReturnAccountsList() throws Exception {
        System.out.println("AccountControllerTest: Tüm hesapları getirme testi başlıyor...");

        when(accountService.getAllAccounts(anyInt(), anyInt())).thenReturn(listResponse);

        mockMvc.perform(get("/api/accounts")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hesaplar başarıyla getirildi"))
                .andExpect(jsonPath("$.data[0].accountNumber").value("TR1234567890"))
                .andExpect(jsonPath("$.data[0].customerName").value("Batuhan Yalçın"));

        System.out.println("AccountControllerTest: Tüm hesapları getirme testi başarılı!");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ID'ye göre hesap getirme testi")
    void getAccountById_ShouldReturnAccount() throws Exception {
        System.out.println("AccountControllerTest: ID'ye göre hesap getirme testi başlıyor...");

        when(accountService.getAccountById(anyLong())).thenReturn(singleResponse);
        when(securityService.isOwnerOfAccount(anyLong())).thenReturn(false);

        mockMvc.perform(get("/api/accounts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hesap başarıyla getirildi"))
                .andExpect(jsonPath("$.data.accountNumber").value("TR1234567890"));

        System.out.println("AccountControllerTest: ID'ye göre hesap getirme testi başarılı!");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Hesap numarasına göre hesap getirme testi")
    void getAccountByAccountNumber_ShouldReturnAccount() throws Exception {
        System.out.println("AccountControllerTest: Hesap numarasına göre hesap getirme testi başlıyor...");

        when(accountService.getAccountByAccountNumber(anyString())).thenReturn(singleResponse);
        when(securityService.isOwnerOfAccountByNumber(anyString())).thenReturn(true);

        mockMvc.perform(get("/api/accounts/number/TR1234567890")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hesap başarıyla getirildi"))
                .andExpect(jsonPath("$.data.balance").value(5000.00));

        System.out.println("AccountControllerTest: Hesap numarasına göre hesap getirme testi başarılı!");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Müşteri ID'sine göre hesapları getirme testi")
    void getAccountsByCustomerId_ShouldReturnAccountsList() throws Exception {
        System.out.println("AccountControllerTest: Müşteri ID'sine göre hesapları getirme testi başlıyor...");

        when(accountService.getAccountsByCustomerId(anyLong())).thenReturn(listResponse);
        when(securityService.isCurrentCustomer(anyLong())).thenReturn(true);

        mockMvc.perform(get("/api/accounts/customer/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hesaplar başarıyla getirildi"))
                .andExpect(jsonPath("$.data[0].customerId").value(1));

        System.out.println("AccountControllerTest: Müşteri ID'sine göre hesapları getirme testi başarılı!");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Hesap oluşturma testi")
    void createAccount_ShouldReturnCreatedAccount() throws Exception {
        System.out.println("AccountControllerTest: Hesap oluşturma testi başlıyor...");

        ApiResponse<AccountDTO> createdResponse = ApiResponse.success("Hesap başarıyla oluşturuldu", accountDTO);
        when(accountService.createAccount(any(AccountCreateRequest.class))).thenReturn(createdResponse);
        when(securityService.isCurrentCustomer(anyLong())).thenReturn(true);

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hesap başarıyla oluşturuldu"))
                .andExpect(jsonPath("$.data.accountNumber").value("TR1234567890"));

        System.out.println("AccountControllerTest: Hesap oluşturma testi başarılı!");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Hesap güncelleme testi")
    void updateAccount_ShouldReturnUpdatedAccount() throws Exception {
        System.out.println("AccountControllerTest: Hesap güncelleme testi başlıyor...");

        ApiResponse<AccountDTO> updatedResponse = ApiResponse.success("Hesap başarıyla güncellendi", accountDTO);
        when(accountService.updateAccount(anyLong(), any(AccountDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/accounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hesap başarıyla güncellendi"))
                .andExpect(jsonPath("$.data.accountNumber").value("TR1234567890"));

        System.out.println("AccountControllerTest: Hesap güncelleme testi başarılı!");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Hesap silme testi")
    void deleteAccount_ShouldReturnSuccessMessage() throws Exception {
        System.out.println("AccountControllerTest: Hesap silme testi başlıyor...");

        ApiResponse<String> deleteResponse = ApiResponse.success("Hesap başarıyla silindi");
        when(accountService.deleteAccount(anyLong())).thenReturn(deleteResponse);

        mockMvc.perform(delete("/api/accounts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hesap başarıyla silindi"));

        System.out.println("AccountControllerTest: Hesap silme testi başarılı!");
    }
} 