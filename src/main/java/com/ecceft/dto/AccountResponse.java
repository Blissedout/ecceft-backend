package com.ecceft.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponse {

    private String accountId;
    private String fullName;
    private String accountNumber;
    private BigDecimal balance;
    private String publicKey;
    private String algorithm;
    private String curve;
    private String keySize;
    private LocalDateTime createdAt;
}