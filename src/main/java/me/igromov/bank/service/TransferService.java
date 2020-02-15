package me.igromov.bank.service;

import me.igromov.bank.core.Account;
import me.igromov.bank.dao.AccountDao;
import me.igromov.bank.exception.AccountNotFoundException;
import me.igromov.bank.exception.IllegalBalanceOperationException;

public class TransferService {

    private final AccountDao accountDao;

    public TransferService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     *
     * @param from Source account, money will be withdrawn from this one. Should not have same id with `to`
     * @param to Target account, money will be deposited to this one. Should not have same id with `from`
     * @param amount Amount to transfer, should be > 0
     */
    public void transfer(long from, long to, long amount) {
        Account fromAccount = accountDao.getAccount(from);
        Account toAccount = accountDao.getAccount(to);

        if (fromAccount == null) {
            throw new AccountNotFoundException(from);
        }

        if (toAccount == null) {
            throw new AccountNotFoundException(to);
        }

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
