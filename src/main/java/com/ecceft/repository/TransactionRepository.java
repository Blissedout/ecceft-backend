package com.ecceft.repository;

import com.ecceft.model.Transaction;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    boolean existsByNonce(String nonce);

    Page<Transaction> findBySenderAccountIdOrRecipientAccountId(
            String sender,
            String recipient,
            Pageable pageable
    );

    List<Transaction> findTop3BySenderAccountIdOrRecipientAccountIdOrderByCreatedAtDesc(
            String sender,
            String recipient
    );
}