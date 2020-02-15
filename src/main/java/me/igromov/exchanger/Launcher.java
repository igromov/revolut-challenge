package me.igromov.exchanger;

import io.javalin.Javalin;
import me.igromov.exchanger.controller.TransferController;
import me.igromov.exchanger.dao.AccountDao;
import me.igromov.exchanger.dao.InMemoryAccountDao;
import me.igromov.exchanger.service.TransferService;

public class Launcher {
    private static final int PORT = 7000;

    public static void main(String[] args) {

        Javalin app = Javalin.create()
                .contextPath("exchanger")
                .port(PORT);

        AccountDao accountDao = new InMemoryAccountDao();
        TransferService transferService = new TransferService(accountDao);
        TransferController transferController = new TransferController(app, transferService);

        app.start();
    }
}