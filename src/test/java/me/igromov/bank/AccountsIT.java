package me.igromov.bank;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import me.igromov.bank.controller.AccountCreateRequest;
import org.junit.Assert;
import org.junit.Test;

public class AccountsIT extends BaseIT {

    @Test
    public void createAccountsWithSameId() throws UnirestException {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setId(100L);
        request.setBalance(999L);

        HttpResponse<String> response1 = Unirest.post(url("/account/create"))
                .body(request)
                .asString();

        Assert.assertEquals(200, response1.getStatus());

        HttpResponse<String> response2 = Unirest.post(url("/account/create"))
                .body(request)
                .asString();

        Assert.assertEquals(400, response2.getStatus());
    }
}
