import io.javalin.Handler;

import java.util.Objects;

import io.javalin.Javalin;
import me.igromov.bank.controller.TransferController;
import me.igromov.bank.dao.AccountDao;
import me.igromov.bank.dao.InMemoryAccountDao;
import me.igromov.bank.service.TransferService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Launcher {
    private static final String BASE_URI = "http://localhost:9044/bank/";


    public static void main(String[] args) throws IOException {

        Javalin app = Javalin.create()
                .contextPath("bank")
                .port(7000);

        AccountDao accountDao = new InMemoryAccountDao();
        TransferService transferService = new TransferService(accountDao);
        TransferController transferController = new TransferController(app, transferService);

        app.start();

//        app.get("/hello", ctx -> ctx.html("Hello, Javalin!"));
//        app.get("/users", UserController.fetchAllUsernames);
//        app.get("/users/:id", UserController.fetchById);


    }

}

class User {
    public final int id;
    public final String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }
}


class UserController {
    public static Handler fetchAllUsernames = ctx -> {
        UserDao dao = UserDao.instance();
        Iterable<String> allUsers = dao.getAllUsernames();
        ctx.json(allUsers);
    };

    public static Handler fetchById = ctx -> {
        int id = Integer.parseInt(Objects.requireNonNull(ctx.param("id")));
        UserDao dao = UserDao.instance();
        User user = dao.getUserById(id).get();
        if (user == null) {
            ctx.html("Not Found");
        } else {
            ctx.json(user);
        }
    };
}


class  UserDao {

    private final List<User> users = Arrays.asList(
            new User(0, "Steve Rogers"),
            new User(1, "Tony Stark"),
            new User(2, "Carol Danvers")
    );

    private static UserDao userDao = null;

    private UserDao() {
    }

    static UserDao instance() {
        if (userDao == null) {
            userDao = new UserDao();
        }
        return userDao;
    }

    Optional<User> getUserById(int id) {
        return users.stream().filter(u -> u.id == id).findFirst();
    }

    Iterable<String> getAllUsernames() {
        return users.stream().map(user -> user.name).collect(Collectors.toList());
    }
}