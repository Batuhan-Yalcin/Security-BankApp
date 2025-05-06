package com.batuhanyalcin.BankApp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.batuhanyalcin.BankApp.dto.AccountCreateRequest;
import com.batuhanyalcin.BankApp.dto.AccountDTO;
import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.entity.Account;
import com.batuhanyalcin.BankApp.entity.Customer;
import com.batuhanyalcin.BankApp.repository.AccountRepository;
import com.batuhanyalcin.BankApp.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountService accountService;

    private Customer customer;
    private Account account;
    private AccountDTO accountDTO;
    private AccountCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        System.out.println("AccountServiceTest: Test hazırlıkları başlıyor...");
        
        // Test için müşteri entity'si oluştur
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Batuhan");
        customer.setLastName("Yalçın");
        customer.setEmail("batuhan@yalcin.com");
        customer.setPassword("hashedPassword");
        customer.setPhoneNumber("5551234567");
        customer.setAddress("İstanbul, Türkiye");
        customer.setAccounts(new HashSet<>());
        
        System.out.println("Test müşterisi oluşturuldu: " + customer.getFirstName() + " " + customer.getLastName());

        // Test için hesap entity'si oluştur
        account = new Account();
        account.setId(1L);
        account.setAccountNumber("TR1234567890");
        account.setBalance(new BigDecimal("5000.00"));
        account.setAccountType(Account.AccountType.CHECKING);
        account.setCustomer(customer);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        
        System.out.println("Test hesabı oluşturuldu: " + account.getAccountNumber() + 
                           " (Bakiye: " + account.getBalance() + " TL)");

        // Test için hesap DTO'su oluştur
        accountDTO = new AccountDTO();
        accountDTO.setId(1L);
        accountDTO.setAccountNumber("TR1234567890");
        accountDTO.setAccountType(Account.AccountType.CHECKING);
        accountDTO.setBalance(new BigDecimal("5000.00"));
        accountDTO.setCustomerId(1L);
        accountDTO.setCustomerName("Batuhan Yalçın");
        accountDTO.setCreatedAt(account.getCreatedAt());
        accountDTO.setUpdatedAt(account.getUpdatedAt());
        
        System.out.println("Test hesap DTO'su oluşturuldu: " + accountDTO.getAccountNumber());

        // Test için hesap oluşturma isteği oluştur
        createRequest = new AccountCreateRequest();
        createRequest.setCustomerId(1L);
        createRequest.setAccountType(Account.AccountType.CHECKING);
        createRequest.setInitialBalance(new BigDecimal("1000.00"));
        
        System.out.println("Test hesap oluşturma isteği oluşturuldu: Müşteri ID=" + createRequest.getCustomerId() + 
                           ", Tip=" + createRequest.getAccountType() + 
                           ", Başlangıç bakiyesi=" + createRequest.getInitialBalance() + " TL");
        
        System.out.println("AccountServiceTest: Test hazırlıkları tamamlandı.");
    }

    @Test
    @DisplayName("Tüm hesapları getirme testi")
    void getAllAccounts_ShouldReturnAllAccounts() {
        System.out.println("AccountServiceTest: Tüm hesapları getirme testi başlıyor...");
        
        Page<Account> accountPage = new PageImpl<>(Arrays.asList(account));
        when(accountRepository.findAll(any(PageRequest.class))).thenReturn(accountPage);

        ApiResponse<List<AccountDTO>> response = accountService.getAllAccounts(0, 10);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Hesaplar başarıyla getirildi", response.getMessage());
        assertEquals(1, response.getData().size());
        
        System.out.println(response.getData().size() + " hesap bulundu");
        System.out.println("AccountServiceTest: Tüm hesapları getirme testi başarılı!");
    }

    @Test
    @DisplayName("Hesap ID'sine göre getirme testi")
    void getAccountById_ShouldReturnAccount() {
        System.out.println("AccountServiceTest: Hesap ID'sine göre getirme testi başlıyor...");
        
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        ApiResponse<AccountDTO> response = accountService.getAccountById(1L);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Hesap başarıyla getirildi", response.getMessage());
        assertEquals("TR1234567890", response.getData().getAccountNumber());
        
        System.out.println("Hesap başarıyla getirildi: " + response.getData().getAccountNumber() + 
                           " (ID: " + response.getData().getId() + ")");
        System.out.println("AccountServiceTest: Hesap ID'sine göre getirme testi başarılı!");
    }

    @Test
    @DisplayName("Hesap numarasına göre getirme testi")
    void getAccountByAccountNumber_ShouldReturnAccount() {
        System.out.println("AccountServiceTest: Hesap numarasına göre getirme testi başlıyor...");
        
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        ApiResponse<AccountDTO> response = accountService.getAccountByAccountNumber("TR1234567890");

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Hesap başarıyla getirildi", response.getMessage());
        assertEquals("TR1234567890", response.getData().getAccountNumber());
        
        System.out.println("Hesap başarıyla getirildi: " + response.getData().getAccountNumber());
        System.out.println("AccountServiceTest: Hesap numarasına göre getirme testi başarılı!");
    }

    @Test
    @DisplayName("Müşteri ID'sine göre hesapları getirme testi")
    void getAccountsByCustomerId_ShouldReturnAccounts() {
        System.out.println("AccountServiceTest: Müşteri ID'sine göre hesapları getirme testi başlıyor...");
        
        when(accountRepository.findByCustomerId(anyLong())).thenReturn(Arrays.asList(account));

        ApiResponse<List<AccountDTO>> response = accountService.getAccountsByCustomerId(1L);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Müşteri hesapları başarıyla getirildi", response.getMessage());
        assertEquals(1, response.getData().size());
        
        System.out.println(response.getData().size() + " adet hesap bulundu müşteri ID'si için: 1");
        System.out.println("AccountServiceTest: Müşteri ID'sine göre hesapları getirme testi başarılı!");
    }

    @Test
    @DisplayName("Hesap oluşturma testi")
    void createAccount_ShouldReturnCreatedAccount() {
        System.out.println("AccountServiceTest: Hesap oluşturma testi başlıyor...");
        
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);

        ApiResponse<AccountDTO> response = accountService.createAccount(createRequest);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Hesap başarıyla oluşturuldu", response.getMessage());
        
        verify(accountRepository, times(1)).save(any(Account.class));
        
        System.out.println("Hesap başarıyla oluşturuldu: " + response.getData().getAccountNumber() + 
                           " (Müşteri: " + response.getData().getCustomerName() + ")");
        System.out.println("AccountServiceTest: Hesap oluşturma testi başarılı!");
    }

    @Test
    @DisplayName("Hesap güncelleme testi")
    void updateAccount_ShouldReturnUpdatedAccount() {
        System.out.println("AccountServiceTest: Hesap güncelleme testi başlıyor...");
        
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        ApiResponse<AccountDTO> response = accountService.updateAccount(1L, accountDTO);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Hesap başarıyla güncellendi", response.getMessage());
        
        verify(accountRepository, times(1)).save(any(Account.class));
        
        System.out.println("Hesap başarıyla güncellendi: " + response.getData().getAccountNumber());
        System.out.println("AccountServiceTest: Hesap güncelleme testi başarılı!");
    }

    @Test
    @DisplayName("Hesap silme testi")
    void deleteAccount_ShouldReturnSuccessMessage() {
        System.out.println("AccountServiceTest: Hesap silme testi başlıyor...");
        
        // Bakiyesi sıfır olan hesap oluştur
        Account zeroBalanceAccount = new Account();
        zeroBalanceAccount.setId(2L);
        zeroBalanceAccount.setAccountNumber("TR0987654321");
        zeroBalanceAccount.setBalance(BigDecimal.ZERO);
        zeroBalanceAccount.setAccountType(Account.AccountType.CHECKING);
        zeroBalanceAccount.setCustomer(customer);
        
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(zeroBalanceAccount));

        ApiResponse<String> response = accountService.deleteAccount(2L);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Hesap başarıyla silindi", response.getMessage());
        
        verify(accountRepository, times(1)).delete(any(Account.class));
        
        System.out.println("Hesap başarıyla silindi: ID=2");
        System.out.println("AccountServiceTest: Hesap silme testi başarılı!");
    }
} 