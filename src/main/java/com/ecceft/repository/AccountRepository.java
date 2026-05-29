package com.ecceft.repository;

import com.ecceft.model.Account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
	
    Optional<Account> findByUsername(String username);
    Optional<Account> findByAccountNumber(String accountNumber);
    

    boolean existsByUsername(String username);
    boolean existsByAccountNumber(String accountNumber);
	boolean existsByEmail(String email);
}
