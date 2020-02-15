package me.igromov.bank.exception;

public class IllegalBalanceOperationException extends ApiRuntimeException {
    public IllegalBalanceOperationException(String message) {
        super(message);
    }
}
