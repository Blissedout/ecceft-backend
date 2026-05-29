package com.ecceft.controller;

import com.ecceft.dto.*;
import com.ecceft.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public TransferResponse transfer(
            @RequestBody TransferRequest request
    ) throws Exception {

        return transactionService.transfer(request);
    }

    @GetMapping("/history")
    public Page<TransactionHistory> history(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        return transactionService.getHistory(
                page,
                size
        );
    }

    @GetMapping("/recent")
    public List<TransactionHistory> recent() {

        return transactionService.recentTransactions();
    }
}