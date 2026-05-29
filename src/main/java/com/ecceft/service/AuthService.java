package com.ecceft.service;

import com.ecceft.dto.*;
import com.ecceft.model.Account;
import com.ecceft.model.AccountStatus;
import com.ecceft.repository.AccountRepository;
import com.ecceft.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
//    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EccCryptoService eccCryptoService;
    private final AesKeyStorageService aesKeyStorageService;
    
   
    @Value("${account.initial.balance}")
    private BigDecimal initialBalance;

    
    public RegisterResponse register(RegisterRequest request) throws Exception {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

            // Generate ECC key pair
            KeyPair keyPair = eccCryptoService.generateEccKeyPair();
            String publicKey = eccCryptoService.publicKeyToHex(keyPair.getPublic());
            String privateKey = eccCryptoService.privateKeyToHex(keyPair.getPrivate());

            // Encrypt private key with AES-256-GCM
            String encryptedPrivateKey = aesKeyStorageService.encryptPrivateKey(privateKey);

            // Generate account ID and number
            String accountId = "ACC-%03d" + UUID.randomUUID().toString().substring(0, 8);
            
            String accountNumber = "2024%07d" + System.currentTimeMillis();

            // Create account
            Account account = Account.builder()
                    .accountId(accountId)
                    .accountNumber(accountNumber)
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .balance(initialBalance)
                    .publicKey(publicKey)
                    .encryptedPrivateKey(encryptedPrivateKey)
                    .createdAt(LocalDateTime.now())
                    .status(AccountStatus.ACTIVE)
                    .build();

            accountRepository.save(account);

            return RegisterResponse.builder()
                    .accountId(accountId)
                    .accountNumber(accountNumber)
                    .username(request.getUsername())
                    .publicKey(publicKey)
                    .privateKey(privateKey)
                    .message("Account created successfully")
                    .build();

        } 
    
    
    public LoginResponse login(LoginRequest request) {
    	
    	Account account = accountRepository.findByUsername(request.getUsername())
    			.orElseThrow(() ->
    	new RuntimeException("Invalid credentials"));
        
    	boolean valid = passwordEncoder.matches(request.getPassword(), account.getPasswordHash());
    	
    	
    	if(!valid) {
    		throw new RuntimeException("Invalid credentials");
    	}
    	
    	String token = jwtTokenProvider.generateToken(account.getUsername());
//        UserDetails userDetails = loadUserByUsername(request.getUsername());
//        String token = jwtTokenProvider.generateToken(
//                userDetails,
//                account.getAccountId(),
//                account.getFullName(),
//                account.getAccountNumber()
//        );

        return LoginResponse.builder()
                .token(token)
                .accountId(account.getAccountId())
                .fullName(account.getFullName())
                .accountNumber(account.getAccountNumber())
                .expiresIn(3600L)
                .build();
    }
}
