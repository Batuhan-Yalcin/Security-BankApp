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

import com.batuhanyalcin.BankApp.entity.Role;

@DataJpaTest
@ActiveProfiles("test")
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Rol oluşturma ve kaydetme testi")
    void saveRole_ShouldReturnSavedRole() {
        System.out.println("RoleRepositoryTest: Rol oluşturma ve kaydetme testi başlıyor...");
        
        // Test rolü oluştur
        Role role = new Role();
        role.setName(Role.RoleType.ROLE_USER);
        
        System.out.println("Test rolü oluşturuldu: " + role.getName());
        
        // Rolü kaydet
        Role savedRole = roleRepository.save(role);
        
        // Doğrulamalar
        assertNotNull(savedRole);
        assertNotNull(savedRole.getId());
        assertEquals(Role.RoleType.ROLE_USER, savedRole.getName());
        
        System.out.println("Rol başarıyla kaydedildi: ID=" + savedRole.getId() + 
                           ", Ad=" + savedRole.getName());
        System.out.println("RoleRepositoryTest: Rol oluşturma ve kaydetme testi başarılı!");
    }
    
    @Test
    @DisplayName("İsme göre rol bulma testi")
    void findByName_ShouldReturnRole() {
        System.out.println("RoleRepositoryTest: İsme göre rol bulma testi başlıyor...");
        
        // Test rolü oluştur ve kaydet
        Role role = new Role();
        role.setName(Role.RoleType.ROLE_USER);
        
        roleRepository.save(role);
        System.out.println("Test rolü kaydedildi: " + role.getName());
        
        // İsme göre rolü bul
        Optional<Role> foundRole = roleRepository.findByName(Role.RoleType.ROLE_USER);
        
        // Doğrulamalar
        assertTrue(foundRole.isPresent());
        assertEquals(Role.RoleType.ROLE_USER, foundRole.get().getName());
        
        System.out.println("Rol isim ile başarıyla bulundu: " + foundRole.get().getName());
        System.out.println("RoleRepositoryTest: İsme göre rol bulma testi başarılı!");
    }
    
    @Test
    @DisplayName("Var olmayan isimle rol bulamama testi")
    void findByName_WithNonExistingName_ShouldReturnEmpty() {
        System.out.println("RoleRepositoryTest: Var olmayan isimle rol bulamama testi başlıyor...");
        
        // Var olmayan role adı ile rolü bulmaya çalış (boş veritabanında)
        Optional<Role> foundRole = roleRepository.findByName(Role.RoleType.ROLE_ADMIN);
        
        // Doğrulamalar
        assertFalse(foundRole.isPresent());
        
        System.out.println("Var olmayan rol adı için boş sonuç döndürüldü: ROLE_ADMIN");
        System.out.println("RoleRepositoryTest: Var olmayan isimle rol bulamama testi başarılı!");
    }
    
    @Test
    @DisplayName("İsim ile rol varlığı kontrolü testi")
    void existsByName_ShouldReturnTrue() {
        System.out.println("RoleRepositoryTest: İsim ile rol varlığı kontrolü testi başlıyor...");
        
        // Test rolü oluştur ve kaydet
        Role role = new Role();
        role.setName(Role.RoleType.ROLE_ADMIN);
        
        roleRepository.save(role);
        System.out.println("Test rolü kaydedildi: " + role.getName());
        
        // İsim ile rol varlığını kontrol et
        boolean exists = roleRepository.existsByName(Role.RoleType.ROLE_ADMIN);
        
        // Doğrulamalar
        assertTrue(exists);
        
        System.out.println("Rol adı varlığı doğrulandı: ROLE_ADMIN");
        System.out.println("RoleRepositoryTest: İsim ile rol varlığı kontrolü testi başarılı!");
    }
    
    @Test
    @DisplayName("Var olmayan isim ile rol varlığı kontrolü testi")
    void existsByName_WithNonExistingName_ShouldReturnFalse() {
        System.out.println("RoleRepositoryTest: Var olmayan isim ile rol varlığı kontrolü testi başlıyor...");
        
        // Var olmayan rol adı ile varlık kontrolü
        boolean exists = roleRepository.existsByName(Role.RoleType.ROLE_USER);
        
        // Doğrulamalar
        assertFalse(exists);
        
        System.out.println("Var olmayan rol adı için false döndürüldü: ROLE_USER");
        System.out.println("RoleRepositoryTest: Var olmayan isim ile rol varlığı kontrolü testi başarılı!");
    }
    
    @Test
    @DisplayName("Admin ve User rollerini oluşturma testi")
    void createAdminAndUserRoles_ShouldReturnSavedRoles() {
        System.out.println("RoleRepositoryTest: Admin ve User rollerini oluşturma testi başlıyor...");
        
        // Admin rolü oluştur ve kaydet
        Role adminRole = new Role();
        adminRole.setName(Role.RoleType.ROLE_ADMIN);
        adminRole = roleRepository.save(adminRole);
        
        // User rolü oluştur ve kaydet
        Role userRole = new Role();
        userRole.setName(Role.RoleType.ROLE_USER);
        userRole = roleRepository.save(userRole);
        
        // Doğrulamalar
        assertNotNull(adminRole.getId());
        assertNotNull(userRole.getId());
        assertEquals(Role.RoleType.ROLE_ADMIN, adminRole.getName());
        assertEquals(Role.RoleType.ROLE_USER, userRole.getName());
        
        System.out.println("Admin rolü başarıyla oluşturuldu: ID=" + adminRole.getId());
        System.out.println("User rolü başarıyla oluşturuldu: ID=" + userRole.getId());
        System.out.println("RoleRepositoryTest: Admin ve User rollerini oluşturma testi başarılı!");
    }
} 