package com.batuhanyalcin.BankApp.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batuhanyalcin.BankApp.dto.ApiResponse;
import com.batuhanyalcin.BankApp.dto.CustomerDTO;
import com.batuhanyalcin.BankApp.entity.Customer;
import com.batuhanyalcin.BankApp.entity.Role;
import com.batuhanyalcin.BankApp.exception.DuplicateResourceException;
import com.batuhanyalcin.BankApp.exception.ResourceNotFoundException;
import com.batuhanyalcin.BankApp.repository.CustomerRepository;
import com.batuhanyalcin.BankApp.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public ApiResponse<List<CustomerDTO>> getAllCustomers(int page, int size) {
        Page<Customer> customerPage = customerRepository.findAll(
                PageRequest.of(page, size, Sort.by("firstName").ascending())
        );
        
        List<CustomerDTO> customerDTOs = customerPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ApiResponse.success("Müşteriler başarıyla getirildi", customerDTOs);
    }
    
    public ApiResponse<CustomerDTO> getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri", "id", id));
        
        return ApiResponse.success("Müşteri başarıyla getirildi", convertToDTO(customer));
    }
    
    public ApiResponse<CustomerDTO> getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri", "email", email));
        
        return ApiResponse.success("Müşteri başarıyla getirildi", convertToDTO(customer));
    }
    
    @Transactional
    public ApiResponse<CustomerDTO> createCustomer(CustomerDTO customerDTO) {
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new DuplicateResourceException("Müşteri", "email", customerDTO.getEmail());
        }
        
        Customer customer = new Customer();
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        customer.setAddress(customerDTO.getAddress());
        
        // Şifre için varsayılan değer oluştur
        customer.setPassword(passwordEncoder.encode("Password123"));
        
        // Varsayılan USER rolü ata
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(Role.RoleType.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "name", "ROLE_USER"));
        roles.add(userRole);
        
        // Eğer ADMIN rolü istendiyse ekle
        if (customerDTO.getRoles() != null && customerDTO.getRoles().contains("ADMIN")) {
            Role adminRole = roleRepository.findByName(Role.RoleType.ROLE_ADMIN)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol", "name", "ROLE_ADMIN"));
            roles.add(adminRole);
        }
        
        customer.setRoles(roles);
        
        Customer savedCustomer = customerRepository.save(customer);
        
        return ApiResponse.success("Müşteri başarıyla oluşturuldu", convertToDTO(savedCustomer));
    }
    
    @Transactional
    public ApiResponse<CustomerDTO> updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri", "id", id));
        
        // E-posta adresi değiştirilmek isteniyorsa ve başka bir müşteride varsa hata fırlat
        if (!existingCustomer.getEmail().equals(customerDTO.getEmail()) && 
                customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new DuplicateResourceException("Müşteri", "email", customerDTO.getEmail());
        }
        
        existingCustomer.setFirstName(customerDTO.getFirstName());
        existingCustomer.setLastName(customerDTO.getLastName());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setPhoneNumber(customerDTO.getPhoneNumber());
        existingCustomer.setAddress(customerDTO.getAddress());
        
        // Roller güncellenecekse
        if (customerDTO.getRoles() != null && !customerDTO.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            
            if (customerDTO.getRoles().contains("USER")) {
                Role userRole = roleRepository.findByName(Role.RoleType.ROLE_USER)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol", "name", "ROLE_USER"));
                roles.add(userRole);
            }
            
            if (customerDTO.getRoles().contains("ADMIN")) {
                Role adminRole = roleRepository.findByName(Role.RoleType.ROLE_ADMIN)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol", "name", "ROLE_ADMIN"));
                roles.add(adminRole);
            }
            
            existingCustomer.setRoles(roles);
        }
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        
        return ApiResponse.success("Müşteri başarıyla güncellendi", convertToDTO(updatedCustomer));
    }
    
    @Transactional
    public ApiResponse<String> deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri", "id", id));
        
        customerRepository.delete(customer);
        
        return ApiResponse.success("Müşteri başarıyla silindi");
    }
    
    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setAddress(customer.getAddress());
        
        Set<String> roles = customer.getRoles().stream()
                .map(role -> role.getName().name().replace("ROLE_", ""))
                .collect(Collectors.toSet());
        
        dto.setRoles(roles);
        
        return dto;
    }
} 