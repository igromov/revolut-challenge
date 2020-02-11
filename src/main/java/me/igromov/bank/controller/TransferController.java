package me.igromov.bank.controller;

import io.javalin.Context;
import io.javalin.ExceptionHandler;
import io.javalin.HaltException;
import io.javalin.Javalin;
import me.igromov.bank.exception.AccountNotFoundException;
import me.igromov.bank.exception.DuplicateAccountException;
import me.igromov.bank.exception.IllegalBalanceOperationException;
import me.igromov.bank.service.TransferService;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TransferController {
    private final Javalin router;
    private final TransferService transferService;

    public TransferController(Javalin router, TransferService transferService) {
        this.router = router;
        this.transferService = transferService;

        this.router.get("/balance/:id", this::getBalance);
        this.router.post("/account/create", this::createAccount);

        this.router.exception(AccountNotFoundException.class, getExceptionHandler(400));
        this.router.exception(DuplicateAccountException.class, getExceptionHandler(400));
        this.router.exception(IllegalBalanceOperationException.class, getExceptionHandler(400));
    }

    @NotNull
    private <T extends Exception> ExceptionHandler<T> getExceptionHandler(int statusCode) {
        return (exception, ctx) -> {
            ctx.status(statusCode);
            ctx.result(exception.getMessage());
        };
    }

    private void createAccount(Context ctx) {
        // TODO exception handling?
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

    private long parseLongOrException(String longParam, Supplier<RuntimeException> exceptionProducer) {
        try {
            return Long.parseLong(longParam);
        } catch (NumberFormatException e) {
            throw exceptionProducer.get();
        }
    }

}
