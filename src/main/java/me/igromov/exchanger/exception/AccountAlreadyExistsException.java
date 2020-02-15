package me.igromov.exchanger.exception;

public class AccountAlreadyExistsException extends ApiRuntimeException {
    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}
