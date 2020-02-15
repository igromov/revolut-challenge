package me.igromov.exchanger.core;

import me.igromov.exchanger.exception.IllegalBalanceOperationException;

import java.util.Objects;

public class Account {
    private final long id;
    private long balance;

    public Account(long id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    public synchronized void deposit(long amount) {
        if (amount < 0) {
            throw new IllegalBalanceOperationException("Illegal parameter: amount, should be > 0, but was: " + amount);
        }

        balance = Math.addExact(balance, amount);
    }

    public synchronized void withdraw(long amount) {
        if (amount < 0) {
            throw new IllegalBalanceOperationException("Illegal parameter: amount, should be > 0, but was: " + amount);
        }

        long newBalance = Math.subtractExact(balance, amount);

        if (newBalance < 0) {
            throw new IllegalBalanceOperationException("Tried to decrease balance for account #" + id + " for " + amount + " but it only has " + balance);
        }

        balance = newBalance;
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
