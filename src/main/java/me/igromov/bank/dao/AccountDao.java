package me.igromov.bank.dao;

import me.igromov.bank.core.Account;
import org.jetbrains.annotations.Nullable;

public interface AccountDao {
    /**
     * @param id Account id, should be > 0
     * @param initialBalance Initial account balance, should be >= 0
     */
    void createAccount(long id, long initialBalance);

    @Nullable
    Account getAccount(long id);
}
