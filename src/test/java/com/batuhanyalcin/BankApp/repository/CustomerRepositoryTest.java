package com.batuhanyalcin.BankApp.repository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.batuhanyalcin.BankApp.entity.Customer;

@DataJpaTest
@ActiveProfiles("test")
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("Müşteri oluşturma ve kaydetme testi")
    void saveCustomer_ShouldReturnSavedCustomer() {
        System.out.println("CustomerRepositoryTest: Müşteri oluşturma ve kaydetme testi başlıyor...");
        
        // Test müşterisi oluştur
        Customer customer = new Customer();
        customer.setFirstName("Batuhan");
        customer.setLastName("Yalçın");
        customer.setEmail("batuhan@yalcin.com");
        customer.setPassword("hashedPassword");
        customer.setPhoneNumber("5551234567");
        customer.setAddress("İstanbul, Türkiye");
        
        System.out.println("Test müşterisi oluşturuldu: " + customer.getFirstName() + " " + customer.getLastName());
        
        // Müşteriyi kaydet
        Customer savedCustomer = customerRepository.save(customer);
        
        // Doğrulamalar
        assertNotNull(savedCustomer);
        assertNotNull(savedCustomer.getId());
        assertEquals("Batuhan", savedCustomer.getFirstName());
        assertEquals("Yalçın", savedCustomer.getLastName());
        
        System.out.println("Müşteri başarıyla kaydedildi: ID=" + savedCustomer.getId() + 
                          ", Ad=" + savedCustomer.getFirstName() + 
                          ", Soyad=" + savedCustomer.getLastName());
        System.out.println("CustomerRepositoryTest: Müşteri oluşturma ve kaydetme testi başarılı!");
    }
    
    @Test
    @DisplayName("Email ile müşteri bulma testi")
    void findByEmail_ShouldReturnCustomer() {
        System.out.println("CustomerRepositoryTest: Email ile müşteri bulma testi başlıyor...");
        
        // Test müşterisi oluştur ve kaydet
        Customer customer = new Customer();
        customer.setFirstName("Batuhan");
        customer.setLastName("Yalçın");
        customer.setEmail("batuhan@yalcin.com");
        customer.setPassword("hashedPassword");
        customer.setPhoneNumber("5551234567");
        customer.setAddress("İstanbul, Türkiye");
        
        customerRepository.save(customer);
        System.out.println("Test müşterisi kaydedildi: " + customer.getFirstName() + " " + customer.getLastName());
        
        // Email ile müşteriyi bul
        Optional<Customer> foundCustomer = customerRepository.findByEmail("batuhan@yalcin.com");
        
        // Doğrulamalar
        assertTrue(foundCustomer.isPresent());
        assertEquals("batuhan@yalcin.com", foundCustomer.get().getEmail());
        assertEquals("Batuhan", foundCustomer.get().getFirstName());
        
        System.out.println("Müşteri email ile başarıyla bulundu: " + foundCustomer.get().getEmail());
        System.out.println("CustomerRepositoryTest: Email ile müşteri bulma testi başarılı!");
    }
    
    @Test
    @DisplayName("Var olmayan email ile müşteri bulamama testi")
    void findByEmail_WithNonExistingEmail_ShouldReturnEmpty() {
        System.out.println("CustomerRepositoryTest: Var olmayan email ile müşteri bulamama testi başlıyor...");
        
        // Var olmayan email ile müşteriyi bulmaya çalış
        Optional<Customer> foundCustomer = customerRepository.findByEmail("olmayan@email.com");
        
        // Doğrulamalar
        assertFalse(foundCustomer.isPresent());
        
        System.out.println("Var olmayan email için boş sonuç döndürüldü: olmayan@email.com");
        System.out.println("CustomerRepositoryTest: Var olmayan email ile müşteri bulamama testi başarılı!");
    }
    
    @Test
    @DisplayName("Email ile müşteri varlığı kontrolü testi")
    void existsByEmail_ShouldReturnTrue() {
        System.out.println("CustomerRepositoryTest: Email ile müşteri varlığı kontrolü testi başlıyor...");
        
        // Test müşterisi oluştur ve kaydet
        Customer customer = new Customer();
        customer.setFirstName("Batuhan");
        customer.setLastName("Yalçın");
        customer.setEmail("batuhan@yalcin.com");
        customer.setPassword("hashedPassword");
        customer.setPhoneNumber("5551234567");
        customer.setAddress("İstanbul, Türkiye");
        
        customerRepository.save(customer);
        System.out.println("Test müşterisi kaydedildi: " + customer.getFirstName() + " " + customer.getLastName());
        
        // Email ile müşteri varlığını kontrol et
        boolean exists = customerRepository.existsByEmail("batuhan@yalcin.com");
        
        // Doğrulamalar
        assertTrue(exists);
        
        System.out.println("Müşteri email varlığı doğrulandı: batuhan@yalcin.com");
        System.out.println("CustomerRepositoryTest: Email ile müşteri varlığı kontrolü testi başarılı!");
    }
    
    @Test
    @DisplayName("Var olmayan email ile müşteri varlığı kontrolü testi")
    void existsByEmail_WithNonExistingEmail_ShouldReturnFalse() {
        System.out.println("CustomerRepositoryTest: Var olmayan email ile müşteri varlığı kontrolü testi başlıyor...");
        
        // Var olmayan email ile müşteri varlığını kontrol et
        boolean exists = customerRepository.existsByEmail("olmayan@email.com");
        
        // Doğrulamalar
        assertFalse(exists);
        
        System.out.println("Var olmayan email için false döndürüldü: olmayan@email.com");
        System.out.println("CustomerRepositoryTest: Var olmayan email ile müşteri varlığı kontrolü testi başarılı!");
    }
} 