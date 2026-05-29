package com.ecceft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String accountId;
    private String accountNumber;
    private String username;
    private String publicKey;
    private String privateKey;
    private String message;
}
