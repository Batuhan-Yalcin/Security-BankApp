package com.batuhanyalcin.BankApp.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.batuhanyalcin.BankApp.entity.Account;
import com.batuhanyalcin.BankApp.entity.Customer;

@DataJpaTest
@ActiveProfiles("test")
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;
    private Account account;

    @BeforeEach
    void setUp() {
        System.out.println("AccountRepositoryTest: Test hazırlıkları başlıyor...");
        
        // Test müşterisi oluştur ve kaydet
        customer = new Customer();
        customer.setFirstName("Batuhan");
        customer.setLastName("Yalçın");
        customer.setEmail("batuhan@yalcin.com");
        customer.setPassword("hashedPassword");
        customer.setPhoneNumber("5551234567");
        customer.setAddress("İstanbul, Türkiye");
        
        customer = customerRepository.save(customer);
        System.out.println("Test müşterisi oluşturuldu ve kaydedildi: " + customer.getFirstName() + " " + 
                           customer.getLastName() + " (ID: " + customer.getId() + ")");

        // Test hesabı oluştur ve kaydet
        account = new Account();
        account.setAccountNumber("TR1234567890");
        account.setBalance(new BigDecimal("5000.00"));
        account.setAccountType(Account.AccountType.CHECKING);
        account.setCustomer(customer);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        
        account = accountRepository.save(account);
        System.out.println("Test hesabı oluşturuldu ve kaydedildi: " + account.getAccountNumber() + 
                           " (ID: " + account.getId() + ", Bakiye: " + account.getBalance() + " TL)");
        
        System.out.println("AccountRepositoryTest: Test hazırlıkları tamamlandı.");
    }

    @Test
    @DisplayName("Hesap numarasına göre hesap bulma testi")
    void findByAccountNumber_ShouldReturnAccount() {
        System.out.println("AccountRepositoryTest: Hesap numarasına göre hesap bulma testi başlıyor...");
        
        Optional<Account> foundAccount = accountRepository.findByAccountNumber("TR1234567890");
        
        assertTrue(foundAccount.isPresent());
        assertEquals("TR1234567890", foundAccount.get().getAccountNumber());
        assertEquals(0, new BigDecimal("5000.00").compareTo(foundAccount.get().getBalance()));
        
        System.out.println("Hesap numarasına göre hesap başarıyla bulundu: " + foundAccount.get().getAccountNumber() + 
                           " (Bakiye: " + foundAccount.get().getBalance() + " TL)");
        System.out.println("AccountRepositoryTest: Hesap numarasına göre hesap bulma testi başarılı!");
    }
    
    @Test
    @DisplayName("Müşteriye göre hesapları bulma testi")
    void findByCustomer_ShouldReturnAccounts() {
        System.out.println("AccountRepositoryTest: Müşteriye göre hesapları bulma testi başlıyor...");
        
        List<Account> accounts = accountRepository.findByCustomer(customer);
        
        assertNotNull(accounts);
        assertFalse(accounts.isEmpty());
        assertEquals(1, accounts.size());
        assertEquals("TR1234567890", accounts.get(0).getAccountNumber());
        
        System.out.println(accounts.size() + " adet hesap bulundu müşteri için: " + 
                           customer.getFirstName() + " " + customer.getLastName());
        accounts.forEach(acc -> 
            System.out.println("Hesap: " + acc.getAccountNumber() + " - " + acc.getBalance() + " TL - " + 
                              acc.getAccountType())
        );
        System.out.println("AccountRepositoryTest: Müşteriye göre hesapları bulma testi başarılı!");
    }
    
    @Test
    @DisplayName("Müşteri ID'sine göre hesapları bulma testi")
    void findByCustomerId_ShouldReturnAccounts() {
        System.out.println("AccountRepositoryTest: Müşteri ID'sine göre hesapları bulma testi başlıyor...");
        
        List<Account> accounts = accountRepository.findByCustomerId(customer.getId());
        
        assertNotNull(accounts);
        assertFalse(accounts.isEmpty());
        assertEquals(1, accounts.size());
        assertEquals("TR1234567890", accounts.get(0).getAccountNumber());
        
        System.out.println(accounts.size() + " adet hesap bulundu müşteri ID'si için: " + customer.getId());
        accounts.forEach(acc -> 
            System.out.println("Hesap: " + acc.getAccountNumber() + " - " + acc.getBalance() + " TL - " + 
                              acc.getAccountType())
        );
        System.out.println("AccountRepositoryTest: Müşteri ID'sine göre hesapları bulma testi başarılı!");
    }
    
    @Test
    @DisplayName("Hesap numarası kontrolü testi")
    void existsByAccountNumber_ShouldReturnTrue() {
        System.out.println("AccountRepositoryTest: Hesap numarası kontrolü testi başlıyor...");
        
        boolean exists = accountRepository.existsByAccountNumber("TR1234567890");
        
        assertTrue(exists);
        
        System.out.println("Hesap numarası varlığı doğrulandı: TR1234567890");
        System.out.println("AccountRepositoryTest: Hesap numarası kontrolü testi başarılı!");
    }
    
    @Test
    @DisplayName("Var olmayan hesap numarası kontrolü testi")
    void existsByAccountNumber_WithNonExistingNumber_ShouldReturnFalse() {
        System.out.println("AccountRepositoryTest: Var olmayan hesap numarası kontrolü testi başlıyor...");
        
        boolean exists = accountRepository.existsByAccountNumber("TR0000000000");
        
        assertFalse(exists);
        
        System.out.println("Var olmayan hesap numarası için false döndürüldü: TR0000000000");
        System.out.println("AccountRepositoryTest: Var olmayan hesap numarası kontrolü testi başarılı!");
    }
    
    @Test
    @DisplayName("Müşteri ID'si ve hesap tipine göre hesapları bulma testi")
    void findByCustomerIdAndAccountType_ShouldReturnAccounts() {
        System.out.println("AccountRepositoryTest: Müşteri ID'si ve hesap tipine göre hesapları bulma testi başlıyor...");
        
        List<Account> accounts = accountRepository.findByCustomerIdAndAccountType(
                customer.getId(), Account.AccountType.CHECKING);
        
        assertNotNull(accounts);
        assertFalse(accounts.isEmpty());
        assertEquals(1, accounts.size());
        assertEquals(Account.AccountType.CHECKING, accounts.get(0).getAccountType());
        
        System.out.println(accounts.size() + " adet " + Account.AccountType.CHECKING + 
                          " tipinde hesap bulundu müşteri ID'si için: " + customer.getId());
        accounts.forEach(acc -> 
            System.out.println("Hesap: " + acc.getAccountNumber() + " - " + acc.getBalance() + " TL - " + 
                              acc.getAccountType())
        );
        System.out.println("AccountRepositoryTest: Müşteri ID'si ve hesap tipine göre hesapları bulma testi başarılı!");
    }
    
    @Test
    @DisplayName("Müşteri e-postasına göre hesapları bulma testi")
    void findByCustomerEmail_ShouldReturnAccounts() {
        System.out.println("AccountRepositoryTest: Müşteri e-postasına göre hesapları bulma testi başlıyor...");
        
        List<Account> accounts = accountRepository.findByCustomerEmail(customer.getEmail());
        
        assertNotNull(accounts);
        assertFalse(accounts.isEmpty());
        assertEquals(1, accounts.size());
        assertEquals("TR1234567890", accounts.get(0).getAccountNumber());
        
        System.out.println(accounts.size() + " adet hesap bulundu müşteri e-postası için: " + 
                           customer.getEmail());
        accounts.forEach(acc -> 
            System.out.println("Hesap: " + acc.getAccountNumber() + " - " + acc.getBalance() + " TL - " + 
                              acc.getAccountType())
        );
        System.out.println("AccountRepositoryTest: Müşteri e-postasına göre hesapları bulma testi başarılı!");
    }
    
    @Test
    @DisplayName("Müşterinin hesap sahibi olup olmadığını kontrol etme testi")
    void hasAnyAccount_ShouldReturnTrue() {
        System.out.println("AccountRepositoryTest: Müşterinin hesap sahibi olup olmadığını kontrol etme testi başlıyor...");
        
        boolean hasAccount = accountRepository.hasAnyAccount(customer.getId());
        
        assertTrue(hasAccount);
        
        System.out.println("Müşterinin hesap sahibi olduğu doğrulandı: ID=" + customer.getId());
        System.out.println("AccountRepositoryTest: Müşterinin hesap sahibi olup olmadığını kontrol etme testi başarılı!");
    }
} 