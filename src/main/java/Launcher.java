import io.javalin.Javalin;
import me.igromov.bank.controller.TransferController;
import me.igromov.bank.dao.AccountDao;
import me.igromov.bank.dao.InMemoryAccountDao;
import me.igromov.bank.service.TransferService;

import java.io.IOException;

public class Launcher {
    private static final int PORT = 7000;

    public static void main(String[] args) throws IOException {

        Javalin app = Javalin.create()
                .contextPath("money-transfer")
                .port(PORT);

        AccountDao accountDao = new InMemoryAccountDao();
        TransferService transferService = new TransferService(accountDao);
        TransferController transferController = new TransferController(app, transferService);

        app.start();
    }
}