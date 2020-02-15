package me.igromov.bank.controller;

import io.javalin.Context;
import io.javalin.ExceptionHandler;
import io.javalin.HaltException;
import io.javalin.Javalin;
import me.igromov.bank.exception.AccountNotFoundException;
import me.igromov.bank.exception.ApiRuntimeException;
import me.igromov.bank.service.TransferService;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

public class TransferController {
    private final TransferService transferService;

    public TransferController(Javalin router, TransferService transferService) {
        this.transferService = transferService;

        router.get("/account/balance/:id", this::getBalance);
        router.post("/account/create", this::createAccount);
        router.post("/transfer", this::transfer);

        router.exception(ApiRuntimeException.class, getExceptionHandler(HTTP_BAD_REQUEST));
    }

    @NotNull
    private <T extends Exception> ExceptionHandler<T> getExceptionHandler(int statusCode) {
        return (exception, ctx) -> {
            ctx.status(statusCode);
            ctx.result(exception.getMessage());
        };
    }

    private void createAccount(Context ctx) {
        AccountCreateRequest request = ctx.bodyAsClass(AccountCreateRequest.class);

        Long idParam = request.getId();
        Long balance = request.getBalance();

        if (idParam == null) {
            throw new HaltException(400, "'id' param is not specified");
        }

        transferService.createAccount(idParam, balance);
    }

    private void getBalance(Context ctx) {
        String idParam = ctx.param("id");

        if (idParam == null) {
            throw new HaltException(400, "id param is not specified");
        }

        long id = parseLongOrException(idParam, () -> new AccountNotFoundException(idParam));
        long balance = transferService.getBalance(id);

        ctx.json(balance);
    }

    private void transfer(Context ctx) {
        TransferRequest request = ctx.bodyAsClass(TransferRequest.class);

        transferService.transfer(request.getFrom(), request.getTo(), request.getAmount());
    }

    private long parseLongOrException(String longParam, Supplier<RuntimeException> exceptionProducer) {
        try {
            return Long.parseLong(longParam);
        } catch (NumberFormatException e) {
            throw exceptionProducer.get();
        }
    }

}
