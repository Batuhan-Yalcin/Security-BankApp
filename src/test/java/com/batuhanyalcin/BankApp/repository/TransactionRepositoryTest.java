package com.batuhanyalcin.BankApp.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.batuhanyalcin.BankApp.TestConfig;
import com.batuhanyalcin.BankApp.entity.Account;
import com.batuhanyalcin.BankApp.entity.Customer;
import com.batuhanyalcin.BankApp.entity.Transaction;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(TestConfig.class)
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Customer customer;
    private Account sourceAccount;
    private Account targetAccount;
    private Transaction depositTransaction;
    private Transaction withdrawalTransaction;
    private Transaction transferTransaction;

    @BeforeEach
    void setUp() {
        System.out.println("TransactionRepositoryTest: Test hazırlıkları başlıyor...");
        
        // Önce varolan tüm işlemleri temizle
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();
        
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

        // Test için kaynak hesap oluştur ve kaydet
        sourceAccount = new Account();
        sourceAccount.setAccountNumber("TR1234567890");
        sourceAccount.setBalance(new BigDecimal("5000.00"));
        sourceAccount.setAccountType(Account.AccountType.CHECKING);
        sourceAccount.setCustomer(customer);
        sourceAccount.setCreatedAt(LocalDateTime.now());
        sourceAccount.setUpdatedAt(LocalDateTime.now());
        
        sourceAccount = accountRepository.save(sourceAccount);
        System.out.println("Test kaynak hesabı oluşturuldu ve kaydedildi: " + sourceAccount.getAccountNumber() + 
                           " (ID: " + sourceAccount.getId() + ", Bakiye: " + sourceAccount.getBalance() + " TL)");

        // Test için hedef hesap oluştur ve kaydet
        targetAccount = new Account();
        targetAccount.setAccountNumber("TR0987654321");
        targetAccount.setBalance(new BigDecimal("3000.00"));
        targetAccount.setAccountType(Account.AccountType.SAVINGS);
        targetAccount.setCustomer(customer);
        targetAccount.setCreatedAt(LocalDateTime.now());
        targetAccount.setUpdatedAt(LocalDateTime.now());
        
        targetAccount = accountRepository.save(targetAccount);
        System.out.println("Test hedef hesabı oluşturuldu ve kaydedildi: " + targetAccount.getAccountNumber() + 
                           " (ID: " + targetAccount.getId() + ", Bakiye: " + targetAccount.getBalance() + " TL)");

        // Para yatırma işlemi oluştur ve kaydet
        depositTransaction = new Transaction();
        depositTransaction.setAmount(new BigDecimal("1000.00"));
        depositTransaction.setType(Transaction.TransactionType.DEPOSIT);
        depositTransaction.setDescription("Test para yatırma");
        depositTransaction.setTargetAccount(sourceAccount);
        depositTransaction.setTransactionDate(LocalDateTime.now().minusHours(2));
        
        depositTransaction = transactionRepository.save(depositTransaction);
        System.out.println("Para yatırma işlemi oluşturuldu ve kaydedildi: " + depositTransaction.getType() + 
                           " (ID: " + depositTransaction.getId() + ", Tutar: " + depositTransaction.getAmount() + " TL)");

        // Para çekme işlemi oluştur ve kaydet
        withdrawalTransaction = new Transaction();
        withdrawalTransaction.setAmount(new BigDecimal("500.00"));
        withdrawalTransaction.setType(Transaction.TransactionType.WITHDRAWAL);
        withdrawalTransaction.setDescription("Test para çekme");
        withdrawalTransaction.setSourceAccount(sourceAccount);
        withdrawalTransaction.setTransactionDate(LocalDateTime.now().minusHours(1));
        
        withdrawalTransaction = transactionRepository.save(withdrawalTransaction);
        System.out.println("Para çekme işlemi oluşturuldu ve kaydedildi: " + withdrawalTransaction.getType() + 
                           " (ID: " + withdrawalTransaction.getId() + ", Tutar: " + withdrawalTransaction.getAmount() + " TL)");

        // Transfer işlemi oluştur ve kaydet
        transferTransaction = new Transaction();
        transferTransaction.setAmount(new BigDecimal("1500.00"));
        transferTransaction.setType(Transaction.TransactionType.TRANSFER);
        transferTransaction.setDescription("Test para transferi");
        transferTransaction.setSourceAccount(sourceAccount);
        transferTransaction.setTargetAccount(targetAccount);
        transferTransaction.setTransactionDate(LocalDateTime.now());
        
        transferTransaction = transactionRepository.save(transferTransaction);
        System.out.println("Transfer işlemi oluşturuldu ve kaydedildi: " + transferTransaction.getType() + 
                           " (ID: " + transferTransaction.getId() + ", Tutar: " + transferTransaction.getAmount() + " TL)");
        
        System.out.println("TransactionRepositoryTest: Test hazırlıkları tamamlandı.");
    }

    @Test
    @DisplayName("ID'ye göre işlem getirme testi")
    void findById_ShouldReturnTransaction() {
        System.out.println("TransactionRepositoryTest: ID'ye göre işlem getirme testi başlıyor...");
        
        Optional<Transaction> foundTransaction = transactionRepository.findById(depositTransaction.getId());
        
        assertTrue(foundTransaction.isPresent(), "İşlem bulunamadı");
        assertEquals(depositTransaction.getId(), foundTransaction.get().getId());
        assertEquals(Transaction.TransactionType.DEPOSIT, foundTransaction.get().getType());
        assertEquals(0, new BigDecimal("1000.00").compareTo(foundTransaction.get().getAmount()));
        
        System.out.println("İşlem başarıyla bulundu: " + foundTransaction.get().getType() + 
                           " (ID: " + foundTransaction.get().getId() + 
                           ", Tutar: " + foundTransaction.get().getAmount() + " TL)");
        System.out.println("TransactionRepositoryTest: ID'ye göre işlem getirme testi başarılı!");
    }

    @Test
    @DisplayName("Hesap ID'sine göre işlem listesi getirme testi")
    void findAllByAccountId_ShouldReturnTransactionsList() {
        System.out.println("TransactionRepositoryTest: Hesap ID'sine göre işlem listesi getirme testi başlıyor...");
        
        Page<Transaction> transactions = transactionRepository.findAllByAccountId(
                sourceAccount.getId(), 
                PageRequest.of(0, 10)
        );
        
        assertNotNull(transactions);
        assertTrue(transactions.getTotalElements() > 0, "İşlem bulunamadı");
        
        System.out.println(transactions.getTotalElements() + " adet işlem bulundu hesap ID'si için: " + sourceAccount.getId());
        transactions.getContent().forEach(tx -> 
            System.out.println("İşlem: " + tx.getType() + " - " + tx.getAmount() + " TL - " + tx.getTransactionDate())
        );
        System.out.println("TransactionRepositoryTest: Hesap ID'sine göre işlem listesi getirme testi başarılı!");
    }

    @Test
    @DisplayName("Hesap numarasına göre işlem listesi getirme testi")
    void findAllByAccountNumber_ShouldReturnTransactionsList() {
        System.out.println("TransactionRepositoryTest: Hesap numarasına göre işlem listesi getirme testi başlıyor...");
        
        Page<Transaction> transactions = transactionRepository.findAllByAccountNumber(
                sourceAccount.getAccountNumber(), 
                PageRequest.of(0, 10)
        );
        
        assertNotNull(transactions);
        assertTrue(transactions.getTotalElements() > 0, "İşlem bulunamadı");
        
        System.out.println(transactions.getTotalElements() + " adet işlem bulundu hesap numarası için: " + 
                           sourceAccount.getAccountNumber());
        transactions.getContent().forEach(tx -> 
            System.out.println("İşlem: " + tx.getType() + " - " + tx.getAmount() + " TL - " + tx.getTransactionDate())
        );
        System.out.println("TransactionRepositoryTest: Hesap numarasına göre işlem listesi getirme testi başarılı!");
    }

    @Test
    @DisplayName("Müşteri ID'sine göre işlem listesi getirme testi")
    void findAllByCustomerId_ShouldReturnTransactionsList() {
        System.out.println("TransactionRepositoryTest: Müşteri ID'sine göre işlem listesi getirme testi başlıyor...");
        
        Page<Transaction> transactions = transactionRepository.findAllByCustomerId(
                customer.getId(), 
                PageRequest.of(0, 10)
        );
        
        assertNotNull(transactions);
        assertTrue(transactions.getTotalElements() > 0, "İşlem bulunamadı");
        
        System.out.println(transactions.getTotalElements() + " adet işlem bulundu müşteri ID'si için: " + customer.getId());
        transactions.getContent().forEach(tx -> 
            System.out.println("İşlem: " + tx.getType() + " - " + tx.getAmount() + " TL - " + tx.getTransactionDate())
        );
        System.out.println("TransactionRepositoryTest: Müşteri ID'sine göre işlem listesi getirme testi başarılı!");
    }

    @Test
    @DisplayName("Hesap ID'si ve tarih aralığına göre işlem listesi getirme testi")
    void findAllByAccountIdAndDateBetween_ShouldReturnTransactionsList() {
        System.out.println("TransactionRepositoryTest: Hesap ID'si ve tarih aralığına göre işlem listesi getirme testi başlıyor...");
        
        LocalDateTime startDate = LocalDateTime.now().minusHours(3);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);
        
        List<Transaction> transactions = transactionRepository.findAllByAccountIdAndDateBetween(
                sourceAccount.getId(), startDate, endDate);
        
        assertNotNull(transactions);
        assertTrue(!transactions.isEmpty(), "İşlem bulunamadı");
        
        System.out.println(transactions.size() + " adet işlem bulundu hesap ID'si ve tarih aralığı için: " + 
                           sourceAccount.getId() + ", " + startDate + " - " + endDate);
        transactions.forEach(tx -> 
            System.out.println("İşlem: " + tx.getType() + " - " + tx.getAmount() + " TL - " + tx.getTransactionDate())
        );
        System.out.println("TransactionRepositoryTest: Hesap ID'si ve tarih aralığına göre işlem listesi getirme testi başarılı!");
    }

    @Test
    @DisplayName("Hesap ID'si ve işlem tipine göre işlem listesi getirme testi")
    void findAllByAccountIdAndType_ShouldReturnTransactionsList() {
        System.out.println("TransactionRepositoryTest: Hesap ID'si ve işlem tipine göre işlem listesi getirme testi başlıyor...");
        
        List<Transaction> transactions = transactionRepository.findAllByAccountIdAndType(
                sourceAccount.getId(), Transaction.TransactionType.TRANSFER);
        
        assertNotNull(transactions);
        assertTrue(!transactions.isEmpty(), "İşlem bulunamadı");
        
        System.out.println(transactions.size() + " adet işlem bulundu hesap ID'si ve işlem tipi için: " + 
                           sourceAccount.getId() + ", " + Transaction.TransactionType.TRANSFER);
        transactions.forEach(tx -> 
            System.out.println("İşlem: " + tx.getType() + " - " + tx.getAmount() + " TL - " + tx.getTransactionDate())
        );
        System.out.println("TransactionRepositoryTest: Hesap ID'si ve işlem tipine göre işlem listesi getirme testi başarılı!");
    }
    
    @Test
    @DisplayName("Müşteri ID'si ve tarih aralığına göre işlem listesi getirme testi")
    void findAllByCustomerIdAndDateBetween_ShouldReturnTransactionsList() {
        System.out.println("TransactionRepositoryTest: Müşteri ID'si ve tarih aralığına göre işlem listesi getirme testi başlıyor...");
        
        LocalDateTime startDate = LocalDateTime.now().minusHours(3);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);
        
        List<Transaction> transactions = transactionRepository.findAllByCustomerIdAndDateBetween(
                customer.getId(), startDate, endDate);
        
        assertNotNull(transactions);
        assertTrue(!transactions.isEmpty(), "İşlem bulunamadı");
        
        System.out.println(transactions.size() + " adet işlem bulundu müşteri ID'si ve tarih aralığı için: " + 
                           customer.getId() + ", " + startDate + " - " + endDate);
        transactions.forEach(tx -> 
            System.out.println("İşlem: " + tx.getType() + " - " + tx.getAmount() + " TL - " + tx.getTransactionDate())
        );
        System.out.println("TransactionRepositoryTest: Müşteri ID'si ve tarih aralığına göre işlem listesi getirme testi başarılı!");
    }
    
    @Test
    @DisplayName("Hesaba göre işlem listesi getirme testi")
    void findAllByAccount_ShouldReturnTransactionsList() {
        System.out.println("TransactionRepositoryTest: Hesaba göre işlem listesi getirme testi başlıyor...");
        
        List<Transaction> transactions = transactionRepository.findAllByAccount(sourceAccount);
        
        assertNotNull(transactions);
        assertTrue(!transactions.isEmpty(), "İşlem bulunamadı");
        
        System.out.println(transactions.size() + " adet işlem bulundu hesap için: " + 
                           sourceAccount.getAccountNumber());
        transactions.forEach(tx -> 
            System.out.println("İşlem: " + tx.getType() + " - " + tx.getAmount() + " TL - " + tx.getTransactionDate())
        );
        System.out.println("TransactionRepositoryTest: Hesaba göre işlem listesi getirme testi başarılı!");
    }
} 