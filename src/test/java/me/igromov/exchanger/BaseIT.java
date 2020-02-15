package me.igromov.exchanger;

import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import me.igromov.exchanger.controller.TransferController;
import me.igromov.exchanger.controller.pojo.AccountCreateRequest;
import me.igromov.exchanger.controller.pojo.TransferRequest;
import me.igromov.exchanger.dao.AccountDao;
import me.igromov.exchanger.dao.InMemoryAccountDao;
import me.igromov.exchanger.service.TransferService;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class BaseIT {
    private static final int TEST_PORT = 7001;
    private static final String BASE_URL = "http://localhost:" + TEST_PORT + "/exchanger";

    private static Javalin app;

    public Javalin getApp() {
        return app;
    }

    @BeforeClass
    public static void initApp() {
        app = Javalin.create()
                .contextPath("exchanger")
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

    protected HttpResponse<String> createAccount(long id) {
        return createAccount(id, 0);
    }

    protected HttpResponse<String> createAccount(long id, long balance) {
        AccountCreateRequest request = new AccountCreateRequest(id, balance);

        return Unirest.post(url("/account/create"))
                .body(request)
                .asString();
    }

    protected HttpResponse<String> transfer(long from, long to, long amount) {
        TransferRequest request = new TransferRequest(from, to, amount);

        return Unirest.post(url("/transfer"))
                .body(request)
                .asString();
    }

    protected HttpResponse<String> getBalance(long id) {

        return Unirest.get(url("/account/balance/" + id))
                .asString();
    }

    protected String url(String suffix) {
        return BASE_URL + suffix;
    }
}