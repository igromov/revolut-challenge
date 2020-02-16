package me.igromov.exchanger.it;

import kong.unirest.HttpResponse;
import kong.unirest.UnirestException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.net.HttpURLConnection.*;

public class AccountsIT extends BaseIT {

    @Test
    public void createAccountsWithSameIdTest() throws UnirestException {
        HttpResponse<String> response1 = createAccount(100L, 999L);
        Assert.assertEquals(response1.getBody(), HTTP_OK, response1.getStatus());

        HttpResponse<String> response2 = createAccount(100L, 999L);
        Assert.assertEquals(response2.getBody(), HTTP_BAD_REQUEST, response2.getStatus());
    }

    @Test
    public void createDifferentAccountsTest() {
        Map<Long, HttpResponse<String>> responses = LongStream.rangeClosed(101, 200)
                .boxed()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> createAccount(id, 999))
                );

        responses.forEach((id, response) -> {
            Assert.assertEquals(
                    "Status != 200 for request #" + id,
                    HTTP_OK,
                    response.getStatus()
            );
        });
    }
}