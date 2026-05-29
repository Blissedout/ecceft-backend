package com.ecceft.controller;

import com.ecceft.dto.AccountResponse;
import com.ecceft.dto.PrivateKeyResponse;
import com.ecceft.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me")
    public AccountResponse getMyAccount() {

        return accountService.getCurrentAccount();
    }

    @GetMapping("/me/private-key")
    public PrivateKeyResponse getPrivateKey()
            throws Exception {

        return accountService.getPrivateKey();
    }
}