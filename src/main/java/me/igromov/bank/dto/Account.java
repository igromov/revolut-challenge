package me.igromov.bank.dto;

import me.igromov.bank.exception.IllegalBalanceOperationException;

import java.util.concurrent.atomic.AtomicLong;

public class Account {
    private final long id;
    private final AtomicLong balance;

    public Account(long id, long balance) {
        this.id = id;
        this.balance = new AtomicLong(balance);
    }

    public void deposit(long amountToAdd) {
        if (amountToAdd < 0) {
            throw new IllegalBalanceOperationException("Tried to increase balance for account #" + id + ", but amountToAdd is negative: " + amountToAdd);
        }

        balance.addAndGet(amountToAdd);
    }

    public void withdraw(long amountToSubtract) {
        if (amountToSubtract < 0) {
            throw new IllegalBalanceOperationException("Tried to decrease balance for account #" + id + ", but amountToSubtract is negative: " + amountToSubtract);
        }

        balance.addAndGet(-amountToSubtract);
    }

    public long getBalance() {
        return balance.get();
    }
}
