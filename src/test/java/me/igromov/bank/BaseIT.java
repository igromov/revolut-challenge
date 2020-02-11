package me.igromov.bank;

import io.javalin.Javalin;
import me.igromov.bank.controller.TransferController;
import me.igromov.bank.dao.AccountDao;
import me.igromov.bank.dao.InMemoryAccountDao;
import me.igromov.bank.service.TransferService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class BaseIT {
    private static final int TEST_PORT = 7001;
    private static final String BASE_URL = "http://localhost:" + TEST_PORT + "/money-transfer";

    private static Javalin app;

    public Javalin getApp() {
        return app;
    }

    @BeforeClass
    public static void initApp() {
        app = Javalin.create()
                .contextPath("money-transfer")
                .port(TEST_PORT);

        AccountDao accountDao = new InMemoryAccountDao();
        TransferService transferService = new TransferService(accountDao);
        TransferController transferController = new TransferController(app, transferService);

        app.start();
    }

    @AfterClass
    public static void shutdownApp() {
        app.stop();

        app = null;
    }

    protected String url(String suffix) {
        return BASE_URL + suffix;
    }
}