package me.igromov.exchanger.it;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.javalin.Javalin;
import me.igromov.exchanger.controller.ExchangeController;
import me.igromov.exchanger.controller.entity.AccountCreateRequest;
import me.igromov.exchanger.controller.entity.TransferRequest;
import me.igromov.exchanger.dao.AccountDao;
import me.igromov.exchanger.dao.InMemoryAccountDao;
import me.igromov.exchanger.service.ExchangeService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class BaseIT {
    private static final int TEST_PORT = 7001;
    private static final String BASE_URL = "http://localhost:" + TEST_PORT + "/exchanger";

    private static Javalin app;

    @BeforeClass
    public static void initApp() {
        Unirest.setObjectMapper(new ObjectMapper() {
            com.fasterxml.jackson.databind.ObjectMapper mapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public String writeValue(Object value) {
                try {
                    return mapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            }

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return mapper.readValue(value, valueType);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        app = Javalin.create()
                .contextPath("exchanger")
                .port(TEST_PORT);

        AccountDao accountDao = new InMemoryAccountDao();
        ExchangeService exchangeService = new ExchangeService(accountDao);
        ExchangeController exchangeController = new ExchangeController(app, exchangeService);

        app.start();
    }

    @AfterClass
    public static void shutdownApp() {
        app.stop();

        app = null;
    }

    protected HttpResponse<String> createAccount(long id) throws UnirestException {
        return createAccount(id, 0);
    }

    protected HttpResponse<String> createAccount(long id, long balance) throws UnirestException {
        AccountCreateRequest request = new AccountCreateRequest(id, balance);

        return Unirest.post(url("/account/create"))
                .body(request)
                .asString();
    }

    protected HttpResponse<String> transfer(long from, long to, long amount) throws UnirestException {
        TransferRequest request = new TransferRequest(from, to, amount);

        return Unirest.post(url("/transfer"))
                .body(request)
                .asString();
    }

    protected HttpResponse<String> getBalance(long id) throws UnirestException {

        return Unirest.get(url("/account/balance/" + id))
                .asString();
    }

    protected String url(String suffix) {
        return BASE_URL + suffix;
    }
}