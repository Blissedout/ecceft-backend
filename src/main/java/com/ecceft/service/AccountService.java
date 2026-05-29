package com.ecceft.service;

import com.ecceft.dto.AccountResponse;
import com.ecceft.dto.PrivateKeyResponse;
import com.ecceft.model.Account;
import com.ecceft.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AesKeyStorageService aesKeyStorageService;

    private Account getAuthenticatedAccount() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return accountRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));
    }

    public AccountResponse getCurrentAccount() {

        Account account = getAuthenticatedAccount();

        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .fullName(account.getFullName())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .publicKey(account.getPublicKey())
                .algorithm("ECDSA")
                .curve("NIST P-256 (secp256r1)")
                .keySize("256-bit")
                .createdAt(account.getCreatedAt())
                .build();
    }

    public PrivateKeyResponse getPrivateKey()
            throws Exception {

        Account account = getAuthenticatedAccount();

        String decrypted =
                aesKeyStorageService.decryptPrivateKey(
                        account.getEncryptedPrivateKey()
                );

        return new PrivateKeyResponse(decrypted);
    }
}