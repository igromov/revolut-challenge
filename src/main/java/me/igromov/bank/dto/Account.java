package me.igromov.bank.dto;

import me.igromov.bank.exception.IllegalBalanceOperationException;

import java.util.Objects;

public class Account {
    private final long id;
    private long balance;

    public Account(long id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    public synchronized void deposit(long amountToAdd) {
        if (amountToAdd < 0) {
            throw new IllegalBalanceOperationException("Tried to increase balance for account #" + id + ", but amountToAdd is negative: " + amountToAdd);
        }

        balance = Math.addExact(balance, amountToAdd);
    }

    public synchronized void withdraw(long amountToSubtract) {
        if (amountToSubtract < 0) {
            throw new IllegalBalanceOperationException("Tried to decrease balance for account #" + id + ", but amountToSubtract is negative: " + amountToSubtract);
        }

        balance = Math.subtractExact(balance, amountToSubtract);
    }

    public synchronized long getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
