package com.ecceft.service;

import com.ecceft.dto.*;
import com.ecceft.exception.InsufficientFundException;
import com.ecceft.exception.ReplayAttackExceptions;
import com.ecceft.exception.SignatureInvalidException;
import com.ecceft.model.*;
import com.ecceft.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final EccCryptoService eccCryptoService;
    
    private final AuditService auditService;

    private Account getSender() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return accountRepository
                .findByUsername(username)
                .orElseThrow();
    }

    @Transactional
    public TransferResponse transfer(
            TransferRequest request
    ) throws Exception {

        Account sender = getSender();

        Account recipient =
                accountRepository
                        .findByAccountNumber(
                                request.getRecipientAccountNumber()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recipient not found"
                                ));

        String canonicalData =
                sender.getAccountId()
                        + "|"
                        + request.getRecipientAccountNumber()
                        + "|"
                        + request.getAmount()
                        + "|"
                        + request.getTimestamp()
                        + "|"
                        + request.getNonce();

        boolean valid =
                eccCryptoService.verifySignature(
                        sender.getPublicKey(),
                        canonicalData,
                        request.getSignatureR(),
                        request.getSignatureS()
                );

        if (!valid) {
        	
        	auditService.logEvent("INVALID_SIGNATURE", sender.getAccountId(), 
        			null, "Signature verification failed", 
        			request.getSignatureR(), 
        			request.getSignatureS()
        			); 
        	
        	throw new SignatureInvalidException(
        			"Transaction signature verfification failed");
        	}

        boolean nonceExists =
                transactionRepository
                        .existsByNonce(request.getNonce());

        if (nonceExists) {
        	
        	auditService.logEvent("REPLAY_ATTACK", 
        			sender.getAccountId(),
        			null, "Duplicate nonce detected", 
        			request.getSignatureR(), request.getSignatureS());
        	

        	throw new ReplayAttackExceptions(
                    "Duplicate nonce detected"
            );
        }

        if (sender.getBalance()
                .compareTo(request.getAmount()) < 0) {

        	throw new InsufficientFundException( 
                    "Insufficient funds"
            );
        }

        sender.setBalance(
                sender.getBalance()
                        .subtract(request.getAmount())
        );

        recipient.setBalance(
                recipient.getBalance()
                        .add(request.getAmount())
        );

        accountRepository.save(sender);

        accountRepository.save(recipient);

        String transactionHash =
                sha256(canonicalData);

        Transaction transaction =
                Transaction.builder()
                        .transactionId(
                                "TXN-"
                                        + UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8)
                        )
                        .senderAccountId(
                                sender.getAccountId()
                        )
                        .recipientAccountId(
                                recipient.getAccountId()
                        )
                        .amount(request.getAmount())
                        .description(request.getDescription())
                        .signatureR(request.getSignatureR())
                        .signatureS(request.getSignatureS())
                        .transactionHash(transactionHash)
                        .nonce(request.getNonce())
                        .status(TransactionStatus.COMPLETED)
                        .createdAt(LocalDateTime.now())
                        .build();

        transactionRepository.save(transaction);
        
        auditService.logEvent("TRANSFER_COMPLETED", sender.getAccountId(), 
        		transaction.getTransactionId(), 
        		"Transfer completed successfully", 
        		request.getSignatureR(), 
        		request.getSignatureS());

        return TransferResponse.builder()
                .transactionId(transaction.getTransactionId())
                .status("COMPLETED")
                .amount(transaction.getAmount())
                .newBalance(sender.getBalance())
                .timestamp(transaction.getCreatedAt())
                .message("Transfer completed successfully")
                .build();
    }

    private String sha256(String data)
            throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

        byte[] hash =
                digest.digest(data.getBytes());

        StringBuilder sb =
                new StringBuilder();

        for (byte b : hash) {

            sb.append(
                    String.format("%02x", b)
            );
        }

        return sb.toString();
    }

    public Page<TransactionHistory> getHistory(
            int page,
            int size
    ) {

        Account account = getSender();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt").descending()
                );

        Page<Transaction> transactions =
                transactionRepository
                        .findBySenderAccountIdOrRecipientAccountId(
                                account.getAccountId(),
                                account.getAccountId(),
                                pageable
                        );

        return transactions.map(tx -> {

            String type =
                    tx.getSenderAccountId()
                            .equals(account.getAccountId())
                            ? "SENT"
                            : "RECEIVED";

            return TransactionHistory.builder()
                    .transactionId(tx.getTransactionId())
                    .type(type)
                    .amount(tx.getAmount())
                    .description(tx.getDescription())
                    .signatureR(tx.getSignatureR())
                    .signatureS(tx.getSignatureS())
                    .nonce(tx.getNonce())
                    .status(tx.getStatus().name())
                    .timestamp(tx.getCreatedAt())
                    .build();
        });
    }

    public List<TransactionHistory> recentTransactions() {

        Account account = getSender();

        List<Transaction> transactions =
                transactionRepository
                        .findTop3BySenderAccountIdOrRecipientAccountIdOrderByCreatedAtDesc(
                                account.getAccountId(),
                                account.getAccountId()
                        );

        List<TransactionHistory> result =
                new ArrayList<>();

        for (Transaction tx : transactions) {

            String type =
                    tx.getSenderAccountId()
                            .equals(account.getAccountId())
                            ? "SENT"
                            : "RECEIVED";

            result.add(
                    TransactionHistory.builder()
                            .transactionId(tx.getTransactionId())
                            .type(type)
                            .amount(tx.getAmount())
                            .description(tx.getDescription())
                            .status(tx.getStatus().name())
                            .timestamp(tx.getCreatedAt())
                            .build()
            );
        }

        return result;
    }
}