package com.batuhanyalcin.BankApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.batuhanyalcin.BankApp.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(Role.RoleType name);
    
    boolean existsByName(Role.RoleType name);
} 