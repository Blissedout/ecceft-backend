package com.ecceft.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    private String recipientAccountNumber;

    private BigDecimal amount;

    private String description;

    private String timestamp;

    private String nonce;

    private String signatureR;

    private String signatureS;
}