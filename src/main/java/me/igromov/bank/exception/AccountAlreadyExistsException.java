package me.igromov.bank.exception;

public class AccountAlreadyExistsException extends ApiRuntimeException {
    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}
