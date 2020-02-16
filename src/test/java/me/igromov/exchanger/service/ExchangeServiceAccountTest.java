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

public class ExchangeServiceAccountTest {
    private ExchangeService exchangeService;

    @Before
    public void setUp() throws Exception {
        AccountDao accountDao = new InMemoryAccountDao();
        exchangeService = new ExchangeService(accountDao);
    }

    @After
    public void tearDown() throws Exception {
        exchangeService = null;
    }

    @Test(expected = AccountAlreadyExistsException.class)
    public void createDuplicateAccountTest() {
        exchangeService.createAccount(1, 100);
        exchangeService.createAccount(1, 100);
    }

    @Test
    public void createValidAccountsTest() {
        exchangeService.createAccount(1, 100);
        exchangeService.createAccount(2, 100);
    }

    @Test(expected = InvalidAccountParametersException.class)
    public void createAccountWithNegativeBalanceTest() {
        exchangeService.createAccount(1, -100);
    }

    @Test
    public void createAccountWithZeroBalanceTest() {
        exchangeService.createAccount(1, 0);
    }

    @Test(expected = InvalidAccountParametersException.class)
    public void createAccountWithNegativeIdTest() {
        exchangeService.createAccount(-1, 100);
    }

    @Test
    public void createMultipleAccountsTest() {
        IntStream.rangeClosed(1, 1000)
                .parallel()
                .forEach(id -> exchangeService.createAccount(id, 100));
    }

    @Test(expected = AccountNotFoundException.class)
    public void getBalanceFromInvalidAccountTest() {
        exchangeService.getBalance(-1);
    }

    @Test(expected = AccountNotFoundException.class)
    public void getBalanceFromNonExistentAccountTest() {
        exchangeService.getBalance(100);
    }

    @Test
    public void getBalanceTest() {
        exchangeService.createAccount(1, 100);
        long actual = exchangeService.getBalance(1);

        Assert.assertEquals(100, actual);
    }
}