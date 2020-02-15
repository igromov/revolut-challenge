package me.igromov.bank.exception;

public class ApiRuntimeException extends RuntimeException {
    public ApiRuntimeException(String message) {
        super(message);
    }
}
