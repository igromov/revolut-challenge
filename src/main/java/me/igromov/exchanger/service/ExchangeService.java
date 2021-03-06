package me.igromov.exchanger.service;

import me.igromov.exchanger.core.Account;
import me.igromov.exchanger.dao.AccountDao;
import me.igromov.exchanger.exception.AccountNotFoundException;
import me.igromov.exchanger.exception.IllegalBalanceOperationException;
import org.jetbrains.annotations.NotNull;

public class ExchangeService {

    private final AccountDao accountDao;

    public ExchangeService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * @param from   Source account, money will be withdrawn from this one. Should not have same id with `to`
     * @param to     Target account, money will be deposited to this one. Should not have same id with `from`
     * @param amount Amount to transfer, should be > 0
     */
    public void transfer(long from, long to, long amount) {
        Account fromAccount = getAccountOrThrowException(from);
        Account toAccount = getAccountOrThrowException(to);

        if (from == to) {
            throw new IllegalBalanceOperationException("Illegal parameter: to / from accounts should be different, but were: #" + from);
        }

        final Account lockOne = from < to ? fromAccount : toAccount;
        final Account lockTwo = from > to ? fromAccount : toAccount;

        synchronized (lockOne) {
            synchronized (lockTwo) {
                fromAccount.withdraw(amount);
                toAccount.deposit(amount);
            }
        }
    }

    public void withdraw(long accountId, long amount) {
        Account account = getAccountOrThrowException(accountId);

        account.withdraw(amount);
    }

    public void deposit(long accountId, long amount) {
        Account account = getAccountOrThrowException(accountId);

        account.deposit(amount);
    }

    @NotNull
    private Account getAccountOrThrowException(long accountId) {
        Account account = accountDao.getAccount(accountId);

        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }

        return account;
    }

    public long getBalance(long accountId) {
        Account account = getAccountOrThrowException(accountId);

        return account.getBalance();
    }

    public void createAccount(long id, long initialBalance) {
        accountDao.createAccount(id, initialBalance);
    }
}
