package me.igromov.bank.dao;

import me.igromov.bank.dto.Account;
import me.igromov.bank.exception.DuplicateAccountException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryAccountDao implements AccountDao {
    private final ConcurrentMap<Long, Account> accountMap = new ConcurrentHashMap<>();

    @Override
    public void createAccount(@NotNull Long id, long initialBalance) {
        Account account = new Account(id, initialBalance);
        Account oldAcc = accountMap.putIfAbsent(id, account);

        if (oldAcc != null) {
            throw new DuplicateAccountException("Duplicate account #" + id + " was not created");
        }
    }

    @Override
    @Nullable
    public Account getAccount(@NotNull Long id) {
        return accountMap.get(id);
    }
}