package com.ecceft.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SignatureInvalidException.class)
    public ResponseEntity<?> handleSignature(
            SignatureInvalidException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of(
                                "error",
                                "SIGNATURE_INVALID",

                                "message",
                                ex.getMessage(),

                                "timestamp",
                                LocalDateTime.now()
                        )
                );
    }

    @ExceptionHandler(ReplayAttackExceptions.class)
    public ResponseEntity<?> handleReplay(
            ReplayAttackExceptions ex
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        Map.of(
                                "error",
                                "REPLAY_DETECTED",

                                "message",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(InsufficientFundException.class)
    public ResponseEntity<?> handleFunds(
            InsufficientFundException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of(
                                "error",
                                "INSUFFICIENT_FUNDS",

                                "message",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(
            Exception ex
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of(
                                "error",
                                "INTERNAL_ERROR",

                                "message",
                                ex.getMessage()
                        )
                );
    }
}