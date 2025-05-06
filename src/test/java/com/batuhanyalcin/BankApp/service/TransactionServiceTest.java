package com.batuhanyalcin.BankApp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
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
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.dto.DepositRequest;
import com.batuhanyalcin.BankApp.dto.TransactionDTO;
import com.batuhanyalcin.BankApp.dto.TransferRequest;
import com.batuhanyalcin.BankApp.dto.WithdrawRequest;
import com.batuhanyalcin.BankApp.entity.Account;
import com.batuhanyalcin.BankApp.entity.Customer;
import com.batuhanyalcin.BankApp.entity.Transaction;
import com.batuhanyalcin.BankApp.repository.AccountRepository;
import com.batuhanyalcin.BankApp.repository.CustomerRepository;
import com.batuhanyalcin.BankApp.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Customer customer;
    private Account account;
    private Account secondAccount;
    private Transaction transaction;
    private DepositRequest depositRequest;
    private WithdrawRequest withdrawRequest;
    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        System.out.println("TransactionServiceTest: Test hazırlıkları başlıyor...");
        
        // Test için müşteri oluştur
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Batuhan");
        customer.setLastName("Yalçın");
        customer.setEmail("batuhan@yalcin.com");
        
        System.out.println("Test müşterisi oluşturuldu: " + customer.getFirstName() + " " + customer.getLastName());

        // Test için hesap oluştur
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

        // İkinci test hesabı oluştur
        secondAccount = new Account();
        secondAccount.setId(2L);
        secondAccount.setAccountNumber("TR0987654321");
        secondAccount.setBalance(new BigDecimal("3000.00"));
        secondAccount.setAccountType(Account.AccountType.SAVINGS);
        secondAccount.setCustomer(customer);
        secondAccount.setCreatedAt(LocalDateTime.now());
        secondAccount.setUpdatedAt(LocalDateTime.now());
        
        System.out.println("İkinci test hesabı oluşturuldu: " + secondAccount.getAccountNumber() + 
                           " (Bakiye: " + secondAccount.getBalance() + " TL)");

        // Test için işlem oluştur
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("1000.00"));
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setDescription("Test para yatırma");
        transaction.setTargetAccount(account);
        transaction.setTransactionDate(LocalDateTime.now());
        
        System.out.println("Test işlemi oluşturuldu: " + transaction.getType() + " - " + 
                           transaction.getAmount() + " TL");

        // Test için para yatırma isteği oluştur
        depositRequest = new DepositRequest();
        depositRequest.setAccountNumber("TR1234567890");
        depositRequest.setAmount(new BigDecimal("1000.00"));
        depositRequest.setDescription("Test para yatırma");
        
        System.out.println("Test para yatırma isteği oluşturuldu: " + depositRequest.getAccountNumber() + 
                           " hesabına " + depositRequest.getAmount() + " TL");

        // Test için para çekme isteği oluştur
        withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAccountNumber("TR1234567890");
        withdrawRequest.setAmount(new BigDecimal("500.00"));
        withdrawRequest.setDescription("Test para çekme");
        
        System.out.println("Test para çekme isteği oluşturuldu: " + withdrawRequest.getAccountNumber() + 
                           " hesabından " + withdrawRequest.getAmount() + " TL");

        // Test için transfer isteği oluştur
        transferRequest = new TransferRequest();
        transferRequest.setSourceAccountNumber("TR1234567890");
        transferRequest.setTargetAccountNumber("TR0987654321");
        transferRequest.setAmount(new BigDecimal("1500.00"));
        transferRequest.setDescription("Test para transferi");
        
        System.out.println("Test para transfer isteği oluşturuldu: " + transferRequest.getSourceAccountNumber() + 
                           " hesabından " + transferRequest.getTargetAccountNumber() + " hesabına " + 
                           transferRequest.getAmount() + " TL");
        
        System.out.println("TransactionServiceTest: Test hazırlıkları tamamlandı.");
    }

    @Test
    @DisplayName("Para yatırma testi")
    void deposit_ShouldReturnCreatedTransaction() {
        System.out.println("TransactionServiceTest: Para yatırma testi başlıyor...");
        
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        ApiResponse<TransactionDTO> response = transactionService.deposit(depositRequest);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Para yatırma işlemi başarılı", response.getMessage());
        assertEquals(Transaction.TransactionType.DEPOSIT, response.getData().getType());
        
        System.out.println("Para yatırma işlemi başarılı: " + response.getData().getAmount() + 
                           " TL " + response.getData().getTargetAccountNumber() + " hesabına yatırıldı");
        System.out.println("Para yatırma sonrası bakiye: " + account.getBalance() + " TL");
        System.out.println("TransactionServiceTest: Para yatırma testi başarılı!");
    }

    @Test
    @DisplayName("Para çekme testi")
    void withdraw_ShouldReturnCreatedTransaction() {
        System.out.println("TransactionServiceTest: Para çekme testi başlıyor...");
        
        // Para çekme işlemi için işlem nesnesini güncelle
        transaction.setType(Transaction.TransactionType.WITHDRAWAL);
        transaction.setSourceAccount(account);
        transaction.setTargetAccount(null);
        transaction.setAmount(new BigDecimal("500.00"));
        
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        ApiResponse<TransactionDTO> response = transactionService.withdraw(withdrawRequest);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Para çekme işlemi başarılı", response.getMessage());
        assertEquals(Transaction.TransactionType.WITHDRAWAL, response.getData().getType());
        
        System.out.println("Para çekme işlemi başarılı: " + response.getData().getAmount() + 
                           " TL " + response.getData().getSourceAccountNumber() + " hesabından çekildi");
        System.out.println("Para çekme sonrası bakiye: " + account.getBalance() + " TL");
        System.out.println("TransactionServiceTest: Para çekme testi başarılı!");
    }

    @Test
    @DisplayName("Para transferi testi")
    void transfer_ShouldReturnCreatedTransaction() {
        System.out.println("TransactionServiceTest: Para transferi testi başlıyor...");
        
        // Transfer işlemi için işlem nesnesini güncelle
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setSourceAccount(account);
        transaction.setTargetAccount(secondAccount);
        transaction.setAmount(new BigDecimal("1500.00"));
        
        when(accountRepository.findByAccountNumber("TR1234567890")).thenReturn(Optional.of(account));
        when(accountRepository.findByAccountNumber("TR0987654321")).thenReturn(Optional.of(secondAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        ApiResponse<TransactionDTO> response = transactionService.transfer(transferRequest);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Para transferi başarılı", response.getMessage());
        assertEquals(Transaction.TransactionType.TRANSFER, response.getData().getType());
        
        System.out.println("Para transferi işlemi başarılı: " + response.getData().getAmount() + 
                           " TL " + response.getData().getSourceAccountNumber() + " hesabından " + 
                           response.getData().getTargetAccountNumber() + " hesabına transfer edildi");
        System.out.println("Kaynak hesap bakiyesi: " + account.getBalance() + " TL");
        System.out.println("Hedef hesap bakiyesi: " + secondAccount.getBalance() + " TL");
        System.out.println("TransactionServiceTest: Para transferi testi başarılı!");
    }

    @Test
    @DisplayName("İşlem getirme testi")
    void getTransactionById_ShouldReturnTransaction() {
        System.out.println("TransactionServiceTest: İşlem getirme testi başlıyor...");
        
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(transaction));

        ApiResponse<TransactionDTO> response = transactionService.getTransactionById(1L);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("İşlem başarıyla getirildi", response.getMessage());
        
        System.out.println("İşlem başarıyla getirildi: ID=" + response.getData().getId() + 
                           ", Tür=" + response.getData().getType() + 
                           ", Tutar=" + response.getData().getAmount() + " TL");
        System.out.println("TransactionServiceTest: İşlem getirme testi başarılı!");
    }

    @Test
    @DisplayName("Hesaba göre işlem listesi getirme testi")
    void getTransactionsByAccountNumber_ShouldReturnTransactionsList() {
        System.out.println("TransactionServiceTest: Hesaba göre işlem listesi getirme testi başlıyor...");
        
        Page<Transaction> transactionPage = new PageImpl<>(Arrays.asList(transaction));
        when(transactionRepository.findAllByAccountNumber(anyString(), any(PageRequest.class))).thenReturn(transactionPage);

        ApiResponse<List<TransactionDTO>> response = transactionService.getTransactionsByAccountNumber("TR1234567890", 0, 10);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Hesap işlemleri başarıyla getirildi", response.getMessage());
        assertEquals(1, response.getData().size());
        
        System.out.println(response.getData().size() + " adet işlem bulundu hesap için: TR1234567890");
        response.getData().forEach(txDto -> 
            System.out.println("İşlem: " + txDto.getType() + " - " + txDto.getAmount() + " TL - " + 
                               txDto.getTransactionDate())
        );
        System.out.println("TransactionServiceTest: Hesaba göre işlem listesi getirme testi başarılı!");
    }
    
    @Test
    @DisplayName("Hesap numarası ve tarih aralığına göre işlem listesi getirme testi")
    void getTransactionsByAccountNumberAndDateBetween_ShouldReturnTransactionsList() {
        System.out.println("TransactionServiceTest: Hesap numarası ve tarih aralığına göre işlem listesi getirme testi başlıyor...");
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(transactionRepository.findAllByAccountIdAndDateBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(transaction));

        ApiResponse<List<TransactionDTO>> response = transactionService.getTransactionsByAccountNumberAndDateBetween(
                "TR1234567890", startDate, endDate);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Belirtilen tarih aralığındaki hesap işlemleri başarıyla getirildi", response.getMessage());
        assertEquals(1L, response.getData().size());
        
        System.out.println(response.getData().size() + " adet işlem bulundu hesap için tarih aralığında: TR1234567890, " + 
                          startDate + " - " + endDate);
        response.getData().forEach(txDto -> 
            System.out.println("İşlem: " + txDto.getType() + " - " + txDto.getAmount() + " TL - " + 
                              txDto.getTransactionDate())
        );
        System.out.println("TransactionServiceTest: Hesap numarası ve tarih aralığına göre işlem listesi getirme testi başarılı!");
    }
    
    @Test
    @DisplayName("Müşteri ID'si ve tarih aralığına göre işlem listesi getirme testi")
    void getTransactionsByCustomerIdAndDateBetween_ShouldReturnTransactionsList() {
        System.out.println("TransactionServiceTest: Müşteri ID'si ve tarih aralığına göre işlem listesi getirme testi başlıyor...");
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        when(customerRepository.existsById(anyLong())).thenReturn(true);
        when(transactionRepository.findAllByCustomerIdAndDateBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(transaction));

        ApiResponse<List<TransactionDTO>> response = transactionService.getTransactionsByCustomerIdAndDateBetween(
                1L, startDate, endDate);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Belirtilen tarih aralığındaki müşteri işlemleri başarıyla getirildi", response.getMessage());
        assertEquals(1, response.getData().size());
        
        System.out.println(response.getData().size() + " adet işlem bulundu müşteri için tarih aralığında: " + 
                          customer.getFirstName() + " " + customer.getLastName() + ", " + startDate + " - " + endDate);
        response.getData().forEach(txDto -> 
            System.out.println("İşlem: " + txDto.getType() + " - " + txDto.getAmount() + " TL - " + 
                              txDto.getTransactionDate())
        );
        System.out.println("TransactionServiceTest: Müşteri ID'si ve tarih aralığına göre işlem listesi getirme testi başarılı!");
    }
    
    @Test
    @DisplayName("Hesap numarası ve işlem tipine göre işlem listesi getirme testi")
    void getTransactionsByAccountNumberAndType_ShouldReturnTransactionsList() {
        System.out.println("TransactionServiceTest: Hesap numarası ve işlem tipine göre işlem listesi getirme testi başlıyor...");
        
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(transactionRepository.findAllByAccountIdAndType(anyLong(), any(Transaction.TransactionType.class)))
            .thenReturn(Arrays.asList(transaction));

        ApiResponse<List<TransactionDTO>> response = transactionService.getTransactionsByAccountNumberAndType(
                "TR1234567890", Transaction.TransactionType.DEPOSIT);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Belirtilen tipteki hesap işlemleri başarıyla getirildi", response.getMessage());
        assertEquals(1, response.getData().size());
        assertEquals(Transaction.TransactionType.DEPOSIT, response.getData().get(0).getType());
        
        System.out.println(response.getData().size() + " adet " + Transaction.TransactionType.DEPOSIT + 
                          " tipinde işlem bulundu hesap için: TR1234567890");
        response.getData().forEach(txDto -> 
            System.out.println("İşlem: " + txDto.getType() + " - " + txDto.getAmount() + " TL - " + 
                              txDto.getTransactionDate())
        );
        System.out.println("TransactionServiceTest: Hesap numarası ve işlem tipine göre işlem listesi getirme testi başarılı!");
    }

    @Test
    @DisplayName("Tüm işlemleri getirme testi")
    void getAllTransactions_ShouldReturnAllTransactions() {
        System.out.println("TransactionServiceTest: Tüm işlemleri getirme testi başlıyor...");
        
        Page<Transaction> transactionPage = new PageImpl<>(Arrays.asList(transaction));
        when(transactionRepository.findAll(any(PageRequest.class))).thenReturn(transactionPage);

        ApiResponse<List<TransactionDTO>> response = transactionService.getAllTransactions(0, 10);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("İşlemler başarıyla getirildi", response.getMessage());
        assertEquals(1, response.getData().size());
        
        System.out.println(response.getData().size() + " işlem bulundu");
        System.out.println("TransactionServiceTest: Tüm işlemleri getirme testi başarılı!");
    }
} 