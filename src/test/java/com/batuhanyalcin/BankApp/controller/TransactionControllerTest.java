package com.batuhanyalcin.BankApp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.dto.DepositRequest;
import com.batuhanyalcin.BankApp.dto.TransactionDTO;
import com.batuhanyalcin.BankApp.dto.TransferRequest;
import com.batuhanyalcin.BankApp.dto.WithdrawRequest;
import com.batuhanyalcin.BankApp.entity.Transaction;
import com.batuhanyalcin.BankApp.security.service.SecurityService;
import com.batuhanyalcin.BankApp.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private SecurityService securityService;

    private TransactionDTO transactionDTO;
    private DepositRequest depositRequest;
    private WithdrawRequest withdrawRequest;
    private TransferRequest transferRequest;
    private ApiResponse<List<TransactionDTO>> listResponse;
    private ApiResponse<TransactionDTO> singleResponse;

    @BeforeEach
    void setUp() {
        System.out.println("TransactionControllerTest: Test hazırlıkları başlıyor...");
        
        // Test için işlem DTO'su oluştur
        transactionDTO = new TransactionDTO();
        transactionDTO.setId(1L);
        transactionDTO.setAmount(new BigDecimal("1000.00"));
        transactionDTO.setTransactionDate(LocalDateTime.now());
        transactionDTO.setType(Transaction.TransactionType.DEPOSIT);
        transactionDTO.setDescription("Test işlemi");
        transactionDTO.setTargetAccountNumber("TR1234567890");
        transactionDTO.setCustomerName("Batuhan Yalçın");
        
        System.out.println("Test işlemi oluşturuldu: " + transactionDTO.getType() + " - " + 
                           transactionDTO.getAmount() + " TL - " + transactionDTO.getCustomerName());

        // Para yatırma isteği oluştur
        depositRequest = new DepositRequest();
        depositRequest.setAccountNumber("TR1234567890");
        depositRequest.setAmount(new BigDecimal("1000.00"));
        depositRequest.setDescription("Para yatırma test işlemi");
        
        System.out.println("Para yatırma isteği oluşturuldu: " + depositRequest.getAccountNumber() + 
                           " hesabına " + depositRequest.getAmount() + " TL");

        // Para çekme isteği oluştur
        withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAccountNumber("TR1234567890");
        withdrawRequest.setAmount(new BigDecimal("500.00"));
        withdrawRequest.setDescription("Para çekme test işlemi");
        
        System.out.println("Para çekme isteği oluşturuldu: " + withdrawRequest.getAccountNumber() + 
                           " hesabından " + withdrawRequest.getAmount() + " TL");

        // Transfer isteği oluştur
        transferRequest = new TransferRequest();
        transferRequest.setSourceAccountNumber("TR1234567890");
        transferRequest.setTargetAccountNumber("TR0987654321");
        transferRequest.setAmount(new BigDecimal("1500.00"));
        transferRequest.setDescription("Para transferi test işlemi");
        
        System.out.println("Para transfer isteği oluşturuldu: " + transferRequest.getSourceAccountNumber() + 
                           " hesabından " + transferRequest.getTargetAccountNumber() + " hesabına " + 
                           transferRequest.getAmount() + " TL");

        // Mock yanıtları hazırla
        listResponse = ApiResponse.success("İşlemler başarıyla getirildi", Arrays.asList(transactionDTO));
        singleResponse = ApiResponse.success("İşlem başarıyla getirildi", transactionDTO);
        
        System.out.println("TransactionControllerTest: Test hazırlıkları tamamlandı.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Tüm işlemleri getirme testi")
    void getAllTransactions_ShouldReturnTransactionsList() throws Exception {
        System.out.println("TransactionControllerTest: Tüm işlemleri getirme testi başlıyor...");
        
        when(transactionService.getAllTransactions(anyInt(), anyInt())).thenReturn(listResponse);

        mockMvc.perform(get("/api/transactions")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("İşlemler başarıyla getirildi"))
                .andExpect(jsonPath("$.data[0].customerName").value("Batuhan Yalçın"))
                .andExpect(jsonPath("$.data[0].amount").value(1000.00));

        System.out.println("TransactionControllerTest: Tüm işlemleri getirme testi başarılı!");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Para yatırma testi")
    void deposit_ShouldReturnCreatedTransaction() throws Exception {
        System.out.println("TransactionControllerTest: Para yatırma testi başlıyor...");
        
        when(transactionService.deposit(any(DepositRequest.class))).thenReturn(singleResponse);
        when(securityService.isOwnerOfAccountByNumber(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/transactions/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("İşlem başarıyla getirildi"))
                .andExpect(jsonPath("$.data.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.data.amount").value(1000.00));

        System.out.println("TransactionControllerTest: Para yatırma testi başarılı!");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Para çekme testi")
    void withdraw_ShouldReturnCreatedTransaction() throws Exception {
        System.out.println("TransactionControllerTest: Para çekme testi başlıyor...");
        
        transactionDTO.setType(Transaction.TransactionType.WITHDRAWAL);
        transactionDTO.setAmount(new BigDecimal("500.00"));
        singleResponse = ApiResponse.success("İşlem başarıyla getirildi", transactionDTO);
        
        when(transactionService.withdraw(any(WithdrawRequest.class))).thenReturn(singleResponse);
        when(securityService.isOwnerOfAccountByNumber(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/transactions/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("İşlem başarıyla getirildi"))
                .andExpect(jsonPath("$.data.type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.data.amount").value(500.00));

        System.out.println("TransactionControllerTest: Para çekme testi başarılı!");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Para transferi testi")
    void transfer_ShouldReturnCreatedTransaction() throws Exception {
        System.out.println("TransactionControllerTest: Para transferi testi başlıyor...");
        
        transactionDTO.setType(Transaction.TransactionType.TRANSFER);
        transactionDTO.setAmount(new BigDecimal("1500.00"));
        transactionDTO.setSourceAccountNumber("TR1234567890");
        transactionDTO.setTargetAccountNumber("TR0987654321");
        singleResponse = ApiResponse.success("İşlem başarıyla getirildi", transactionDTO);
        
        when(transactionService.transfer(any(TransferRequest.class))).thenReturn(singleResponse);
        when(securityService.isOwnerOfAccountByNumber(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("İşlem başarıyla getirildi"))
                .andExpect(jsonPath("$.data.type").value("TRANSFER"))
                .andExpect(jsonPath("$.data.amount").value(1500.00));

        System.out.println("TransactionControllerTest: Para transferi testi başarılı!");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Hesaba göre işlem getirme testi")
    void getTransactionsByAccountNumber_ShouldReturnTransactionsList() throws Exception {
        System.out.println("TransactionControllerTest: Hesaba göre işlem getirme testi başlıyor...");
        
        when(transactionService.getTransactionsByAccountNumber(anyString(), anyInt(), anyInt())).thenReturn(listResponse);
        when(securityService.isOwnerOfAccountByNumber(anyString())).thenReturn(true);

        mockMvc.perform(get("/api/transactions/account/TR1234567890")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("İşlemler başarıyla getirildi"))
                .andExpect(jsonPath("$.data[0].customerName").value("Batuhan Yalçın"));

        System.out.println("TransactionControllerTest: Hesaba göre işlem getirme testi başarılı!");
    }
    
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Tarih aralığına göre hesap işlemlerini getirme testi")
    void getTransactionsByAccountNumberAndDateBetween_ShouldReturnTransactionsList() throws Exception {
        System.out.println("TransactionControllerTest: Tarih aralığına göre hesap işlemlerini getirme testi başlıyor...");
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        when(transactionService.getTransactionsByAccountNumberAndDateBetween(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(listResponse);
        when(securityService.isOwnerOfAccountByNumber(anyString())).thenReturn(true);

        mockMvc.perform(get("/api/transactions/account/TR1234567890/daterange")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("İşlemler başarıyla getirildi"))
                .andExpect(jsonPath("$.data[0].customerName").value("Batuhan Yalçın"));

        System.out.println("TransactionControllerTest: Tarih aralığına göre hesap işlemlerini getirme testi başarılı!");
    }
    
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Tarih aralığına göre müşteri işlemlerini getirme testi")
    void getTransactionsByCustomerIdAndDateBetween_ShouldReturnTransactionsList() throws Exception {
        System.out.println("TransactionControllerTest: Tarih aralığına göre müşteri işlemlerini getirme testi başlıyor...");
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        when(transactionService.getTransactionsByCustomerIdAndDateBetween(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(listResponse);
        when(securityService.isCurrentCustomer(anyLong())).thenReturn(true);

        mockMvc.perform(get("/api/transactions/customer/1/daterange")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("İşlemler başarıyla getirildi"))
                .andExpect(jsonPath("$.data[0].customerName").value("Batuhan Yalçın"));

        System.out.println("TransactionControllerTest: Tarih aralığına göre müşteri işlemlerini getirme testi başarılı!");
    }
    
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("İşlem tipine göre hesap işlemlerini getirme testi")
    void getTransactionsByAccountNumberAndType_ShouldReturnTransactionsList() throws Exception {
        System.out.println("TransactionControllerTest: İşlem tipine göre hesap işlemlerini getirme testi başlıyor...");
        
        when(transactionService.getTransactionsByAccountNumberAndType(
                anyString(), any(Transaction.TransactionType.class))).thenReturn(listResponse);
        when(securityService.isOwnerOfAccountByNumber(anyString())).thenReturn(true);

        mockMvc.perform(get("/api/transactions/account/TR1234567890/type/DEPOSIT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("İşlemler başarıyla getirildi"))
                .andExpect(jsonPath("$.data[0].type").value("DEPOSIT"));

        System.out.println("TransactionControllerTest: İşlem tipine göre hesap işlemlerini getirme testi başarılı!");
    }
} 