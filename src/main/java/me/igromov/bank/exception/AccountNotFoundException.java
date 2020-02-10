package me.igromov.bank.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String id) {
        super("Account not found: #" + id);
    }

    public AccountNotFoundException(long id) {
        this(Long.toString(id));
    }
}
