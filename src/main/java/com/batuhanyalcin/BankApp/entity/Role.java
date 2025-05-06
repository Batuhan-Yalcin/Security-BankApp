package com.batuhanyalcin.BankApp.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Rol tipi boş olamaz")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;
    
    @ManyToMany(mappedBy = "roles")
    private Set<Customer> customers = new HashSet<>();
    
    public enum RoleType {
        ROLE_USER,
        ROLE_ADMIN
    }
} 