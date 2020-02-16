package me.igromov.exchanger.dao;

import me.igromov.exchanger.core.Account;
import me.igromov.exchanger.exception.AccountAlreadyExistsException;
import me.igromov.exchanger.exception.InvalidAccountParametersException;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryAccountDao implements AccountDao {
    private final ConcurrentMap<Long, Account> accountMap = new ConcurrentHashMap<>();

    @Override
    public void createAccount(long id, long initialBalance) {
        if (initialBalance < 0) {
            throw new InvalidAccountParametersException("Initial balance should be >= 0, but was " + initialBalance);
        }

        if (id <= 0) {
            throw new InvalidAccountParametersException("Account id should be > 0, but was " + id);
        }

        Account account = new Account(id, initialBalance);
        Account oldAcc = accountMap.putIfAbsent(id, account);

        if (oldAcc != null) {
            throw new AccountAlreadyExistsException("Duplicate account #" + id + " was not created");
        }
    }

    @Override
    @Nullable
    public Account getAccount(long id) {
        return accountMap.get(id);
    }
}
