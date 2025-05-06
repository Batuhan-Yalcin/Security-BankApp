package com.batuhanyalcin.BankApp.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.dto.CustomerDTO;
import com.batuhanyalcin.BankApp.entity.Customer;
import com.batuhanyalcin.BankApp.entity.Role;
import com.batuhanyalcin.BankApp.repository.CustomerRepository;
import com.batuhanyalcin.BankApp.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;
    private Role userRole;

    @BeforeEach
    void setUp() {
        System.out.println("CustomerServiceTest: Test hazırlıkları başlıyor...");
        
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
        customer.setAccounts(new HashSet<>());
        customer.setRoles(new HashSet<>());
        
        System.out.println("Test müşterisi oluşturuldu: " + customer.getFirstName() + " " + customer.getLastName());

        // Test için müşteri DTO'su oluştur
        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setFirstName("Batuhan");
        customerDTO.setLastName("Yalçın");
        customerDTO.setEmail("batuhan@yalcin.com");
        customerDTO.setPhoneNumber("5551234567");
        customerDTO.setAddress("İstanbul, Türkiye");
        
        System.out.println("CustomerServiceTest: Test hazırlıkları tamamlandı.");
    }

    @Test
    @DisplayName("Müşteri ID'sine göre getirme testi")
    void getCustomerById_ShouldReturnCustomer() {
        System.out.println("CustomerServiceTest: Müşteri ID'sine göre getirme testi başlıyor...");
        
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        ApiResponse<CustomerDTO> response = customerService.getCustomerById(1L);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Müşteri başarıyla getirildi", response.getMessage());
        assertEquals("Batuhan", response.getData().getFirstName());
        assertEquals("Yalçın", response.getData().getLastName());
        
        System.out.println("Müşteri başarıyla getirildi: " + response.getData().getFirstName() + " " + 
                           response.getData().getLastName() + " (ID: " + response.getData().getId() + ")");
        System.out.println("CustomerServiceTest: Müşteri ID'sine göre getirme testi başarılı!");
    }

    @Test
    @DisplayName("Müşteri email'ine göre getirme testi")
    void getCustomerByEmail_ShouldReturnCustomer() {
        System.out.println("CustomerServiceTest: Müşteri email'ine göre getirme testi başlıyor...");
        
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));

        ApiResponse<CustomerDTO> response = customerService.getCustomerByEmail("batuhan@yalcin.com");

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Müşteri başarıyla getirildi", response.getMessage());
        assertEquals("batuhan@yalcin.com", response.getData().getEmail());
        
        System.out.println("Müşteri başarıyla getirildi: " + response.getData().getEmail());
        System.out.println("CustomerServiceTest: Müşteri email'ine göre getirme testi başarılı!");
    }

    @Test
    @DisplayName("Tüm müşterileri getirme testi")
    void getAllCustomers_ShouldReturnAllCustomers() {
        System.out.println("CustomerServiceTest: Tüm müşterileri getirme testi başlıyor...");
        
        Page<Customer> customerPage = new PageImpl<>(Arrays.asList(customer));
        when(customerRepository.findAll(any(PageRequest.class))).thenReturn(customerPage);

        ApiResponse<List<CustomerDTO>> response = customerService.getAllCustomers(0, 10);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Müşteriler başarıyla getirildi", response.getMessage());
        assertEquals(1, response.getData().size());
        
        System.out.println(response.getData().size() + " müşteri bulundu");
        System.out.println("CustomerServiceTest: Tüm müşterileri getirme testi başarılı!");
    }

    @Test
    @DisplayName("Müşteri oluşturma testi")
    void createCustomer_ShouldReturnCreatedCustomer() {
        System.out.println("CustomerServiceTest: Müşteri oluşturma testi başlıyor...");
        
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        
        // ROLE_USER için mock ekle
        when(roleRepository.findByName(Role.RoleType.ROLE_USER)).thenReturn(Optional.of(userRole));

        ApiResponse<CustomerDTO> response = customerService.createCustomer(customerDTO);

        assertNotNull(response);
        assertEquals(true, response.isSuccess());
        assertEquals("Müşteri başarıyla oluşturuldu", response.getMessage());
        
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(roleRepository, times(1)).findByName(Role.RoleType.ROLE_USER);
        
        System.out.println("Müşteri başarıyla oluşturuldu: " + response.getData().getFirstName() + " " + 
                           response.getData().getLastName());
        System.out.println("CustomerServiceTest: Müşteri oluşturma testi başarılı!");
    }
} 