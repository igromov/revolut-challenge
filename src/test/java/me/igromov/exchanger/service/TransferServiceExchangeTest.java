package me.igromov.exchanger.service;

import me.igromov.exchanger.dao.AccountDao;
import me.igromov.exchanger.dao.InMemoryAccountDao;
import me.igromov.exchanger.exception.AccountNotFoundException;
import me.igromov.exchanger.exception.IllegalBalanceOperationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.*;

public class TransferServiceExchangeTest {
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

    @Test(expected = AccountNotFoundException.class)
    public void transferFromToNonExistentAccountsTest() {
        exchangeService.transfer(1, 2, 0);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferToNonExistentAccountsTest() {
        exchangeService.createAccount(1, 100);

        exchangeService.transfer(1, 2, 0);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferFromNonExistentAccountsTest() {
        exchangeService.createAccount(2, 100);

        exchangeService.transfer(1, 2, 0);
    }

    @Test
    public void simpleTransferTest() {
        exchangeService.createAccount(1, 100);
        exchangeService.createAccount(2, 0);

        exchangeService.transfer(1, 2, 100);

        Assert.assertEquals(0, exchangeService.getBalance(1));
        Assert.assertEquals(100, exchangeService.getBalance(2));
    }

    @Test
    public void compoundTransferTest() {
        exchangeService.createAccount(1, 100);
        exchangeService.createAccount(2, 0);

        exchangeService.transfer(1, 2, 50);
        exchangeService.transfer(1, 2, 50);

        Assert.assertEquals(0, exchangeService.getBalance(1));
        Assert.assertEquals(100, exchangeService.getBalance(2));
    }

    @Test(expected = IllegalBalanceOperationException.class)
    public void transferTooMuchMoneyTest() {
        exchangeService.createAccount(1, 100);
        exchangeService.createAccount(2, 0);

        exchangeService.transfer(1, 2, 50);
        exchangeService.transfer(1, 2, 10000000);
    }

    @Test
    public void withdrawTooMuchMoneyTest2() {
        exchangeService.createAccount(1, 100);
        exchangeService.createAccount(2, 0);

        exchangeService.transfer(1, 2, 50);
        try {
            exchangeService.transfer(1, 2, 10000000);
        } catch (RuntimeException e) {
            // ignore
        }

        Assert.assertEquals(50, exchangeService.getBalance(1));
        Assert.assertEquals(50, exchangeService.getBalance(2));
    }

    @Test
    public void simpleMultipleAccountTransferTest() {
        exchangeService.createAccount(1, 100);
        exchangeService.createAccount(2, 100);
        exchangeService.createAccount(3, 0);
        exchangeService.createAccount(4, 0);

        exchangeService.transfer(1, 2, 50);
        exchangeService.transfer(2, 3, 25);
        exchangeService.transfer(3, 4, 10);
        exchangeService.transfer(4, 1, 5);

        Assert.assertEquals(55, exchangeService.getBalance(1));
        Assert.assertEquals(125, exchangeService.getBalance(2));
        Assert.assertEquals(15, exchangeService.getBalance(3));
        Assert.assertEquals(5, exchangeService.getBalance(4));
    }

    @Test
    public void validDepositTest() {
        exchangeService.createAccount(1, 100);

        exchangeService.deposit(1, 100);
        exchangeService.deposit(1, 100);
        exchangeService.deposit(1, 100);

        Assert.assertEquals(HTTP_BAD_REQUEST, exchangeService.getBalance(1));
    }

    @Test
    public void validWithdrawTest() {
        exchangeService.createAccount(1, 100);

        exchangeService.withdraw(1, 100);

        Assert.assertEquals(0, exchangeService.getBalance(1));
    }

    @Test(expected = IllegalBalanceOperationException.class)
    public void withdrawTooMuchMoneyTest() {
        exchangeService.createAccount(1, 100);

        exchangeService.withdraw(1, 50);
        exchangeService.withdraw(1, 51);
    }

    @Test
    public void withdrawDepositTest() {
        exchangeService.createAccount(1, 100);

        exchangeService.withdraw(1, 50);
        exchangeService.deposit(1, 100);

        Assert.assertEquals(150, exchangeService.getBalance(1));
    }

    @Test(expected = ArithmeticException.class)
    public void accountOverflowTest() {
        exchangeService.createAccount(1, Long.MAX_VALUE - 20);

        exchangeService.deposit(1, 100);
    }
}