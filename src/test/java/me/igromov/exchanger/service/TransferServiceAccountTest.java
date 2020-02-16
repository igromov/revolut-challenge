package me.igromov.exchanger.service;

import me.igromov.exchanger.dao.AccountDao;
import me.igromov.exchanger.dao.InMemoryAccountDao;
import me.igromov.exchanger.exception.AccountAlreadyExistsException;
import me.igromov.exchanger.exception.AccountNotFoundException;
import me.igromov.exchanger.exception.InvalidAccountParametersException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

public class TransferServiceAccountTest {
    private TransferService transferService;

    @Before
    public void setUp() throws Exception {
        AccountDao accountDao = new InMemoryAccountDao();
        transferService = new TransferService(accountDao);
    }

    @After
    public void tearDown() throws Exception {
        transferService = null;
    }

    @Test(expected = AccountAlreadyExistsException.class)
    public void createDuplicateAccountTest() {
        transferService.createAccount(1, 100);
        transferService.createAccount(1, 100);
    }

    @Test
    public void createValidAccountsTest() {
        transferService.createAccount(1, 100);
        transferService.createAccount(2, 100);
    }

    @Test(expected = InvalidAccountParametersException.class)
    public void createAccountWithNegativeBalanceTest() {
        transferService.createAccount(1, -100);
    }

    @Test
    public void createAccountWithZeroBalanceTest() {
        transferService.createAccount(1, 0);
    }

    @Test(expected = InvalidAccountParametersException.class)
    public void createAccountWithNegativeIdTest() {
        transferService.createAccount(-1, 100);
    }

    @Test
    public void createMultipleAccountsTest() {
        IntStream.rangeClosed(1, 1000)
                .parallel()
                .forEach(id -> transferService.createAccount(id, 100));
    }

    @Test(expected = AccountNotFoundException.class)
    public void getBalanceFromInvalidAccountTest() {
        transferService.getBalance(-1);
    }

    @Test(expected = AccountNotFoundException.class)
    public void getBalanceFromNonExistentAccountTest() {
        transferService.getBalance(100);
    }

    @Test
    public void getBalanceTest() {
        transferService.createAccount(1, 100);
        long actual = transferService.getBalance(1);

        Assert.assertEquals(100, actual);
    }
}