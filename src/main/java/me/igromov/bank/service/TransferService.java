package me.igromov.bank.service;

import me.igromov.bank.dao.AccountDao;
import me.igromov.bank.dto.Account;
import me.igromov.bank.exception.AccountNotFoundException;
import me.igromov.bank.exception.IllegalBalanceOperationException;

public class TransferService {

    private final AccountDao accountDao;

    public TransferService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void transfer(long from, long to, long amount) {
        Account fromAccount = accountDao.getAccount(from);
        Account toAccount = accountDao.getAccount(to);

        if (fromAccount == null) {
            throw new AccountNotFoundException(from);
        }

        if (toAccount == null) {
            throw new AccountNotFoundException(to);
        }

        if (amount <= 0) {
            // TODO from to to log
            throw new IllegalBalanceOperationException("Amount to transfer must be positive, but was: " + amount);
        }

        if (from == to) {
            throw new IllegalBalanceOperationException("Can't transfer money to the same account #" + from);
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

    public long getBalance(long accountId) {
        Account account = accountDao.getAccount(accountId);

        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }

        return account.getBalance();
    }

    public void createAccount(long id, long initialBalance) {
        accountDao.createAccount(id, initialBalance);
    }
}
