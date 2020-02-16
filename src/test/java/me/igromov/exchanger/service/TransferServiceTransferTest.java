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

public class TransferServiceTransferTest {
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

    @Test(expected = AccountNotFoundException.class)
    public void transferFromToNonExistentAccountsTest() {
        transferService.transfer(1, 2, 0);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferToNonExistentAccountsTest() {
        transferService.createAccount(1, 100);

        transferService.transfer(1, 2, 0);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferFromNonExistentAccountsTest() {
        transferService.createAccount(2, 100);

        transferService.transfer(1, 2, 0);
    }

    @Test
    public void simpleTransferTest() {
        transferService.createAccount(1, 100);
        transferService.createAccount(2, 0);

        transferService.transfer(1, 2, 100);

        Assert.assertEquals(0, transferService.getBalance(1));
        Assert.assertEquals(100, transferService.getBalance(2));
    }

    @Test
    public void compoundTransferTest() {
        transferService.createAccount(1, 100);
        transferService.createAccount(2, 0);

        transferService.transfer(1, 2, 50);
        transferService.transfer(1, 2, 50);

        Assert.assertEquals(0, transferService.getBalance(1));
        Assert.assertEquals(100, transferService.getBalance(2));
    }

    @Test(expected = IllegalBalanceOperationException.class)
    public void transferTooMuchMoneyTest() {
        transferService.createAccount(1, 100);
        transferService.createAccount(2, 0);

        transferService.transfer(1, 2, 50);
        transferService.transfer(1, 2, 10000000);
    }

    @Test
    public void withdrawTooMuchMoneyTest2() {
        transferService.createAccount(1, 100);
        transferService.createAccount(2, 0);

        transferService.transfer(1, 2, 50);
        try {
            transferService.transfer(1, 2, 10000000);
        } catch (RuntimeException e) {
            // ignore
        }

        Assert.assertEquals(50, transferService.getBalance(1));
        Assert.assertEquals(50, transferService.getBalance(2));
    }

    @Test
    public void simpleMultipleAccountTransferTest() {
        transferService.createAccount(1, 100);
        transferService.createAccount(2, 100);
        transferService.createAccount(3, 0);
        transferService.createAccount(4, 0);

        transferService.transfer(1, 2, 50);
        transferService.transfer(2, 3, 25);
        transferService.transfer(3, 4, 10);
        transferService.transfer(4, 1, 5);

        Assert.assertEquals(55, transferService.getBalance(1));
        Assert.assertEquals(125, transferService.getBalance(2));
        Assert.assertEquals(15, transferService.getBalance(3));
        Assert.assertEquals(5, transferService.getBalance(4));
    }

    @Test
    public void validDepositTest() {
        transferService.createAccount(1, 100);

        transferService.deposit(1, 100);
        transferService.deposit(1, 100);
        transferService.deposit(1, 100);

        Assert.assertEquals(HTTP_BAD_REQUEST, transferService.getBalance(1));
    }

    @Test
    public void validWithdrawTest() {
        transferService.createAccount(1, 100);

        transferService.withdraw(1, 100);

        Assert.assertEquals(0, transferService.getBalance(1));
    }

    @Test(expected = IllegalBalanceOperationException.class)
    public void withdrawTooMuchMoneyTest() {
        transferService.createAccount(1, 100);

        transferService.withdraw(1, 50);
        transferService.withdraw(1, 51);
    }

    @Test
    public void withdrawDepositTest() {
        transferService.createAccount(1, 100);

        transferService.withdraw(1, 50);
        transferService.deposit(1, 100);

        Assert.assertEquals(150, transferService.getBalance(1));
    }

    @Test(expected = ArithmeticException.class)
    public void accountOverflowTest() {
        transferService.createAccount(1, Long.MAX_VALUE - 20);

        transferService.deposit(1, 100);
    }
}