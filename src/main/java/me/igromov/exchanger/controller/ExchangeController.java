package me.igromov.exchanger.controller;

import io.javalin.Context;
import io.javalin.ExceptionHandler;
import io.javalin.HaltException;
import io.javalin.Javalin;
import me.igromov.exchanger.controller.entity.AccountCreateRequest;
import me.igromov.exchanger.controller.entity.ChangeBalanceRequest;
import me.igromov.exchanger.controller.entity.TransferRequest;
import me.igromov.exchanger.exception.AccountNotFoundException;
import me.igromov.exchanger.exception.ApiRuntimeException;
import me.igromov.exchanger.service.ExchangeService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class ExchangeController {
    private static final Logger log = LoggerFactory.getLogger(ExchangeController.class);
    private final ExchangeService exchangeService;

    public ExchangeController(Javalin router, ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
        
        router.get("/account/balance/:id", this::getBalance);
        router.post("/account/create", this::createAccount);
        router.post("/account/withdraw", this::withdrawn);
        router.post("/account/deposit", this::deposit);
        router.post("/transfer", this::transfer);

        router.exception(ApiRuntimeException.class, getExceptionHandler(HTTP_BAD_REQUEST));
        router.exception(Exception.class, getExceptionHandler(HTTP_INTERNAL_ERROR));
    }


    private void createAccount(Context ctx) {
        AccountCreateRequest request = parseBodyOrException(ctx, AccountCreateRequest.class);

        Long idParam = request.getId();
        Long balance = request.getBalance();

        if (idParam == null) {
            throw new HaltException(HTTP_BAD_REQUEST, "'id' param is not specified");
        }

        exchangeService.createAccount(idParam, balance);
    }

    private void getBalance(Context ctx) {
        String idParam = ctx.param("id");

        if (idParam == null) {
            throw new HaltException(HTTP_BAD_REQUEST, "id param is not specified");
        }

        long id = parseLongOrException(idParam, () -> new AccountNotFoundException(idParam));
        long balance = exchangeService.getBalance(id);

        ctx.json(balance);
    }

    private void transfer(Context ctx) {
        TransferRequest request = parseBodyOrException(ctx, TransferRequest.class);

        exchangeService.transfer(request.getFrom(), request.getTo(), request.getAmount());
    }

    private void withdrawn(Context ctx) {
        ChangeBalanceRequest request = parseBodyOrException(ctx, ChangeBalanceRequest.class);

        exchangeService.withdraw(request.getId(), request.getAmount());
    }

    private void deposit(Context ctx) {
        ChangeBalanceRequest request = parseBodyOrException(ctx, ChangeBalanceRequest.class);

        exchangeService.deposit(request.getId(), request.getAmount());
    }

    private long parseLongOrException(String longParam, Supplier<RuntimeException> exceptionProducer) {
        try {
            return Long.parseLong(longParam);
        } catch (NumberFormatException e) {
            throw exceptionProducer.get();
        }
    }

    private <T> T parseBodyOrException(Context ctx, Class<T> clazz) {
        try {
            return ctx.bodyAsClass(clazz);
        } catch (Exception e) {
            throw new HaltException(HTTP_BAD_REQUEST, "Malformed request body");
        }
    }

    @NotNull
    private <T extends Exception> ExceptionHandler<T> getExceptionHandler(int statusCode) {
        return (exception, ctx) -> {
            ctx.status(statusCode);
            ctx.result(exception.getMessage());
            log.error("Exception while handling request:\nurl: {}\nmethod: {}\nbody: {}", ctx.url(), ctx.method(), ctx.body(), exception);
        };
    }
}
