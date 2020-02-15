package me.igromov.bank.service;

import me.igromov.bank.dao.AccountDao;
import me.igromov.bank.dao.InMemoryAccountDao;
import me.igromov.bank.exception.AccountAlreadyExistsException;
import me.igromov.bank.exception.AccountNotFoundException;
import me.igromov.bank.exception.IllegalBalanceOperationException;
import me.igromov.bank.exception.InvalidAccountParametersException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TransferServiceTest {
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
    public void accountTest() {
        transferService.createAccount(1, 100);
        transferService.createAccount(1, 100);
    }

    @Test
    public void accountTest2() {
        transferService.createAccount(1, 100);
        transferService.createAccount(2, 100);
    }

    @Test(expected = InvalidAccountParametersException.class)
    public void accountTest3() {
        transferService.createAccount(1, -100);
    }

    @Test
    public void accountTest4() {
        transferService.createAccount(1, 0);
    }

    @Test(expected = InvalidAccountParametersException.class)
    public void accountTest5() {
        transferService.createAccount(-1, 100);
    }

    @Test
    public void accountTest6() {
        IntStream.rangeClosed(1, 1000)
                .parallel()
                .forEach(id -> transferService.createAccount(id, 100));
    }

    @Test(expected = AccountNotFoundException.class)
    public void accountTest7() {
        transferService.getBalance(-1);
    }

    @Test(expected = AccountNotFoundException.class)
    public void accountTest8() {
        transferService.getBalance(100);
    }

    @Test
    public void accountTest9() {
        transferService.createAccount(1, 100);
        long actual = transferService.getBalance(1);

        Assert.assertEquals(100, actual);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferTest() {
        transferService.transfer(1, 2, 0);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferTest1() {
        transferService.createAccount(1, 100);

        transferService.transfer(1, 2, 0);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferTest2() {
        transferService.createAccount(2, 100);

        transferService.transfer(1, 2, 0);
    }

    @Test
    public void transferTest3() {
        transferService.createAccount(1, 100);
        transferService.createAccount(2, 0);

        transferService.transfer(1, 2, 100);

        Assert.assertEquals(0, transferService.getBalance(1));
        Assert.assertEquals(100, transferService.getBalance(2));
    }

    @Test
    public void transferTest4() {
        transferService.createAccount(1, 100);
        transferService.createAccount(2, 0);

        transferService.transfer(1, 2, 50);
        transferService.transfer(1, 2, 50);

        Assert.assertEquals(0, transferService.getBalance(1));
        Assert.assertEquals(100, transferService.getBalance(2));
    }

    @Test(expected = IllegalBalanceOperationException.class)
    public void transferTest5() {
        transferService.createAccount(1, 100);
        transferService.createAccount(2, 0);

        transferService.transfer(1, 2, 50);
        transferService.transfer(1, 2, 10000000);
    }

    @Test
    public void transferTest6() {
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
    public void transferTest7() {
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
    public void transferTest8() {
        IntStream.rangeClosed(1, 1000)
                .parallel()
                .forEach(id -> transferService.createAccount(id, 1000));

        IntStream.rangeClosed(1, 1000)
                .parallel()
                .forEach(id -> Assert.assertEquals(
                        "account #" + id,
                        1000,
                        transferService.getBalance(id)
                ));

        IntStream.rangeClosed(1, 1000)
                .parallel()
                .forEach(
                        id -> transferService.transfer(id, id % 1000 + 1, 100)
                );

        IntStream.rangeClosed(1, 1000)
                .parallel()
                .forEach(id -> Assert.assertEquals(
                        "account #" + id,
                        1000,
                        transferService.getBalance(id)
                ));
    }

    @Test(timeout = 5000)
    public void transferTest9() throws InterruptedException {
        int startAccIdIncl = 1;
        int endAccIdIncl = 1000;

        long initialBalance = 1000;

        IntStream.rangeClosed(startAccIdIncl, endAccIdIncl)
                .parallel()
                .forEach(id -> transferService.createAccount(id, initialBalance));

        IntStream.rangeClosed(startAccIdIncl, endAccIdIncl)
                .parallel()
                .forEach(id -> Assert.assertEquals(
                        "account #" + id,
                        endAccIdIncl,
                        transferService.getBalance(id)
                ));

        int n = 10_000;

        ExecutorService executor = Executors.newFixedThreadPool(30);

        CountDownLatch latch = new CountDownLatch(n);

        for (long idx = 0; idx < n; idx++) {
            long from = idx % endAccIdIncl + startAccIdIncl;
            long to = (idx * 17) % endAccIdIncl + startAccIdIncl;
            long amount = (idx * 23) % 50;

            if (from == to) {
                latch.countDown();
                continue;
            }

            executor.execute(() -> {
                transferService.transfer(from, to, amount);
                latch.countDown();
            });
        }

        latch.await();

        long overallBalance = IntStream.rangeClosed(startAccIdIncl, endAccIdIncl)
                .parallel()
                .mapToLong(id -> transferService.getBalance(id))
                .sum();

        Assert.assertEquals((endAccIdIncl - startAccIdIncl + 1) * initialBalance, overallBalance);
    }


    @Test(timeout = 5000)
    public void transferTest10() throws InterruptedException {
        transferService.createAccount(1, 100);
        transferService.createAccount(2, 100);

        ExecutorService executor = Executors.newFixedThreadPool(20);

        int n = 50;
        CountDownLatch latch = new CountDownLatch(n);

        for (int i = 0; i < n; i++) {
            long from = i % 2 + 1;
            long to = (i + 1) % 2 + 1;

            executor.execute(() -> {
                transferService.transfer(from, to, 3);
                latch.countDown();
            });
        }

        latch.await();

        long b1 = transferService.getBalance(1);
        long b2 = transferService.getBalance(2);

        Assert.assertEquals(100, b1);
        Assert.assertEquals(100, b2);
    }
}