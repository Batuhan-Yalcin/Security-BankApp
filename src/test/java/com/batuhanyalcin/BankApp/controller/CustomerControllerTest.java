package com.batuhanyalcin.BankApp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.batuhanyalcin.BankApp.dto.CustomerDTO;
import com.batuhanyalcin.BankApp.security.service.SecurityService;
import com.batuhanyalcin.BankApp.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private SecurityService securityService;

    private CustomerDTO customerDTO;
    private ApiResponse<List<CustomerDTO>> listResponse;
    private ApiResponse<CustomerDTO> singleResponse;

    @BeforeEach
    void setUp() {
        System.out.println("CustomerControllerTest: Test hazırlıkları başlıyor...");

        // Test için müşteri DTO'su oluştur
        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setFirstName("Batuhan");
        customerDTO.setLastName("Yalçın");
        customerDTO.setEmail("batuhan@yalcin.com");
        customerDTO.setPhoneNumber("5551234567");
        customerDTO.setAddress("İstanbul, Türkiye");
        customerDTO.setPassword("password123");

        System.out.println("Test müşterisi oluşturuldu: " + customerDTO.getFirstName() + " " + customerDTO.getLastName());

        // Mock yanıtları hazırla
        listResponse = ApiResponse.success("Müşteriler başarıyla getirildi", Arrays.asList(customerDTO));
        singleResponse = ApiResponse.success("Müşteri başarıyla getirildi", customerDTO);

        System.out.println("CustomerControllerTest: Test hazırlıkları tamamlandı.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Tüm müşterileri getirme testi")
    void getAllCustomers_ShouldReturnCustomersList() throws Exception {
        System.out.println("CustomerControllerTest: Tüm müşterileri getirme testi başlıyor...");

        when(customerService.getAllCustomers(anyInt(), anyInt())).thenReturn(listResponse);

        mockMvc.perform(get("/api/customers")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Müşteriler başarıyla getirildi"))
                .andExpect(jsonPath("$.data[0].firstName").value("Batuhan"))
                .andExpect(jsonPath("$.data[0].lastName").value("Yalçın"));

        System.out.println("CustomerControllerTest: Tüm müşterileri getirme testi başarılı!");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ID'ye göre müşteri getirme testi")
    void getCustomerById_ShouldReturnCustomer() throws Exception {
        System.out.println("CustomerControllerTest: ID'ye göre müşteri getirme testi başlıyor...");

        when(customerService.getCustomerById(anyLong())).thenReturn(singleResponse);
        when(securityService.isCurrentCustomer(anyLong())).thenReturn(false);

        mockMvc.perform(get("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Müşteri başarıyla getirildi"))
                .andExpect(jsonPath("$.data.firstName").value("Batuhan"))
                .andExpect(jsonPath("$.data.lastName").value("Yalçın"));

        System.out.println("CustomerControllerTest: ID'ye göre müşteri getirme testi başarılı!");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Müşteri oluşturma testi")
    void createCustomer_ShouldReturnCreatedCustomer() throws Exception {
        System.out.println("CustomerControllerTest: Müşteri oluşturma testi başlıyor...");

        // Müşteri oluşturma için doğru bir yanıt hazırla
        ApiResponse<CustomerDTO> createdResponse = ApiResponse.success("Müşteri başarıyla oluşturuldu", customerDTO);
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(createdResponse);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Müşteri başarıyla oluşturuldu"))
                .andExpect(jsonPath("$.data.firstName").value("Batuhan"))
                .andExpect(jsonPath("$.data.lastName").value("Yalçın"));

        System.out.println("CustomerControllerTest: Müşteri oluşturma testi başarılı!");
    }
}