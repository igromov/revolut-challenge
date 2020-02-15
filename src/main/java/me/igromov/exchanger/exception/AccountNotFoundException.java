package me.igromov.exchanger.exception;

public class AccountNotFoundException extends ApiRuntimeException {
    public AccountNotFoundException(String id) {
        super("Account not found: #" + id);
    }

    public AccountNotFoundException(long id) {
        this(Long.toString(id));
    }
}
