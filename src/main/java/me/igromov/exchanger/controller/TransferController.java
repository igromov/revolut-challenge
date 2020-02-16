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
import me.igromov.exchanger.service.TransferService;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

public class TransferController {
    private final TransferService transferService;

    public TransferController(Javalin router, TransferService transferService) {
        this.transferService = transferService;
        router.get("/account/balance/:id", this::getBalance);
        router.post("/account/create", this::createAccount);
        router.post("/account/withdraw", this::withdrawn);
        router.post("/account/deposit", this::deposit);
        router.post("/transfer", this::transfer);

        router.exception(ApiRuntimeException.class, getExceptionHandler(HTTP_BAD_REQUEST));
    }


    private void createAccount(Context ctx) {
        AccountCreateRequest request = parseBodyOrException(ctx, AccountCreateRequest.class);

        Long idParam = request.getId();
        Long balance = request.getBalance();

        if (idParam == null) {
            throw new HaltException(HTTP_BAD_REQUEST, "'id' param is not specified");
        }

        transferService.createAccount(idParam, balance);
    }

    private void getBalance(Context ctx) {
        String idParam = ctx.param("id");

        if (idParam == null) {
            throw new HaltException(HTTP_BAD_REQUEST, "id param is not specified");
        }

        long id = parseLongOrException(idParam, () -> new AccountNotFoundException(idParam));
        long balance = transferService.getBalance(id);

        ctx.json(balance);
    }

    private void transfer(Context ctx) {
        TransferRequest request = parseBodyOrException(ctx, TransferRequest.class);

        transferService.transfer(request.getFrom(), request.getTo(), request.getAmount());
    }

    private void withdrawn(Context ctx) {
        ChangeBalanceRequest request = parseBodyOrException(ctx, ChangeBalanceRequest.class);

        transferService.withdraw(request.getId(), request.getAmount());
    }

    private void deposit(Context ctx) {
        ChangeBalanceRequest request = parseBodyOrException(ctx, ChangeBalanceRequest.class);

        transferService.deposit(request.getId(), request.getAmount());
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
        };
    }
}
