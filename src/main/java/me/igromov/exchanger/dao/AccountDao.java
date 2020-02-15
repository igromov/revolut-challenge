package me.igromov.exchanger.dao;

import me.igromov.exchanger.core.Account;
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
