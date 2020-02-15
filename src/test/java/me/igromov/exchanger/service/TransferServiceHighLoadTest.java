package me.igromov.exchanger.service;

import me.igromov.exchanger.dao.AccountDao;
import me.igromov.exchanger.dao.InMemoryAccountDao;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TransferServiceHighLoadTest {
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

    @Test(timeout = 5000)
    public void withdrawDepositTest5() throws InterruptedException {
        transferService.createAccount(1, 1000);

        ExecutorService executor = Executors.newFixedThreadPool(20);

        int n = 500;
        CountDownLatch latch = new CountDownLatch(2 * n);

        for (int i = 0; i < n; i++) {
            executor.execute(() -> {
                transferService.withdraw(1, 1);
                latch.countDown();
            });
            executor.execute(() -> {
                transferService.deposit(1, 1);
                latch.countDown();
            });
        }

        latch.await();

        Assert.assertEquals(1000, transferService.getBalance(1));
    }
}
