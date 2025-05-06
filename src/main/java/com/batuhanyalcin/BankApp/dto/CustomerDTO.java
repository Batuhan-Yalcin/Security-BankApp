package com.batuhanyalcin.BankApp.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    
    private Long id;
    
    @NotBlank(message = "İsim boş olamaz")
    @Size(min = 2, max = 50, message = "İsim 2-50 karakter arasında olmalıdır")
    private String firstName;
    
    @NotBlank(message = "Soyisim boş olamaz")
    @Size(min = 2, max = 50, message = "Soyisim 2-50 karakter arasında olmalıdır")
    private String lastName;
    
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;
    
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Telefon numarası 10-11 rakamdan oluşmalıdır")
    private String phoneNumber;
    @NotBlank(message = "Şifre alanı boş olamaz")
    private String password;
    
    @Size(max = 255, message = "Adres çok uzun")
    private String address;
    
    private Set<String> roles;
} 