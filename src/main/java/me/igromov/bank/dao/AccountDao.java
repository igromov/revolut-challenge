package me.igromov.bank.dao;

import me.igromov.bank.dto.Account;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AccountDao {
    void createAccount(long id, long initialBalance);

    @Nullable
    Account getAccount(long id);
}
