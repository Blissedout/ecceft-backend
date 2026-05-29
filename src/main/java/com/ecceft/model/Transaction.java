package com.ecceft.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String transactionId;

    private String senderAccountId;

    private String recipientAccountId;

    private BigDecimal amount;

    private String description;

    @Lob
    private String signatureR;

    @Lob
    private String signatureS;

    private String transactionHash;

    @Column(unique = true)
    private String nonce;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private LocalDateTime createdAt;
}