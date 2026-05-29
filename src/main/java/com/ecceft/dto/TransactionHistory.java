package com.ecceft.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionHistory {

    private String transactionId;

    private String type;

    private String senderAccountNumber;

    private String recipientAccountNumber;

    private BigDecimal amount;

    private String description;

    private String signatureR;

    private String signatureS;

    private String nonce;

    private String status;

    private LocalDateTime timestamp;
}