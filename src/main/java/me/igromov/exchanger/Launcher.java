package me.igromov.exchanger;

import io.javalin.Javalin;
import me.igromov.exchanger.controller.ExchangeController;
import me.igromov.exchanger.dao.AccountDao;
import me.igromov.exchanger.dao.InMemoryAccountDao;
import me.igromov.exchanger.service.ExchangeService;

public class Launcher {
    private static final int PORT = 7000;

    public static void main(String[] args) {

        Javalin app = Javalin.create()
                .contextPath("exchanger")
                .port(PORT);

        AccountDao accountDao = new InMemoryAccountDao();
        ExchangeService exchangeService = new ExchangeService(accountDao);
        ExchangeController exchangeController = new ExchangeController(app, exchangeService);

        app.start();
    }
}