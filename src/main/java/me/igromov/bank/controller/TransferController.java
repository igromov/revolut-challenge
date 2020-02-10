package me.igromov.bank.controller;

import io.javalin.Context;
import io.javalin.HaltException;
import io.javalin.Javalin;
import me.igromov.bank.exception.AccountNotFoundException;
import me.igromov.bank.service.TransferService;

public class TransferController {
    private final Javalin router;
    private final TransferService transferService;

    public TransferController(Javalin router, TransferService transferService) {
        this.router = router;
        this.transferService = transferService;

        this.router.get("/balance/:id", this::getBalance);

        this.router.exception(AccountNotFoundException.class, (exception, ctx) -> {
            ctx.status(400);
            ctx.result(exception.getMessage());
        });
    }

    private void createAccount(Context ctx) {

    }

    private void getBalance(Context ctx) {
        String idParam = ctx.param("id");

        if (idParam == null) {
            throw new HaltException(400, "id param is not specified");
        }

        try {
            long id = Long.parseLong(idParam);
            long balance = transferService.getBalance(id);

            ctx.json(balance);
        } catch (NumberFormatException e) {
            throw new AccountNotFoundException(idParam);
        }
    }

    private static class Balance {

    }
}
