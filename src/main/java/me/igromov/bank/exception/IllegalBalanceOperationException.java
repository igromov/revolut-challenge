package me.igromov.bank.exception;

public class IllegalBalanceOperationException extends RuntimeException {
    public IllegalBalanceOperationException(String message) {
        super(message);
    }
}