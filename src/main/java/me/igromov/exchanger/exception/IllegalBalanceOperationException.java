package me.igromov.exchanger.exception;

public class IllegalBalanceOperationException extends ApiRuntimeException {
    public IllegalBalanceOperationException(String message) {
        super(message);
    }
}
