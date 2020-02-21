package me.igromov.exchanger.it;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

public class AccountsIT extends BaseIT {

    @Test
    public void createAccountsWithSameIdTest() throws UnirestException {
        HttpResponse<String> response1 = createAccount(100L, 999L);
        Assert.assertEquals(response1.getBody(), HTTP_OK, response1.getStatus());

        HttpResponse<String> response2 = createAccount(100L, 999L);
        Assert.assertEquals(response2.getBody(), HTTP_BAD_REQUEST, response2.getStatus());
    }

    @Test
    public void createDifferentAccountsTest() throws UnirestException {
        Map<Long, HttpResponse<String>> responses = new HashMap<>();

        for (long id = 101; id <= 200; id++) {
            HttpResponse<String> response = createAccount(id, 999);
            responses.put(id, response);
        }

        responses.forEach((id, response) -> {
            Assert.assertEquals(
                    "Status != 200 for request #" + id,
                    HTTP_OK,
                    response.getStatus()
            );
        });
    }
}
