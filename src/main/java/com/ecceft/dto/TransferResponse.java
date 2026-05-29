package com.ecceft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private String transactionId;
    private String status;
    private BigDecimal amount;
    private BigDecimal newBalance;
    private LocalDateTime timestamp;
    private String message;
}
