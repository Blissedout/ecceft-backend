package com.ecceft.exception;

public class ReplayAttackExceptions
        extends RuntimeException {

    public ReplayAttackExceptions(String message) {
        super(message);
    }
}