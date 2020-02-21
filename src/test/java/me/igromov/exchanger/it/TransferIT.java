package me.igromov.exchanger.it;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.net.HttpURLConnection.*;

public class TransferIT extends BaseIT {

    @Test
    public void transferNonExistentAccountsTest() throws UnirestException {
        HttpResponse<String> response = transfer(1, 2, 100);
        Assert.assertEquals(response.getBody(), HTTP_BAD_REQUEST, response.getStatus());

        createAccount(1, 100);
        HttpResponse<String> response1 = transfer(1, 2, 100);
        Assert.assertEquals(response1.getBody(), HTTP_BAD_REQUEST, response1.getStatus());

        createAccount(2, 100);
        HttpResponse<String> response2 = transfer(1, 2, 100);
        Assert.assertEquals(response2.getBody(), HTTP_OK, response2.getStatus());
    }

    @Test
    public void transferInvalidAmounts() throws UnirestException {
        createAccount(3, 200);
        createAccount(4, 0);

        List<HttpResponse<String>> responses = Arrays.asList(
                transfer(3, 4, 900),
                transfer(3, 4, 150),
                transfer(3, 4, 51),
                transfer(3, 4, 50),
                transfer(3, 4, 1),
                getBalance(3)
        );

        // 200 - 900
        Assert.assertEquals(responses.get(0).getBody(), HTTP_BAD_REQUEST, responses.get(0).getStatus());

        // 200 - 150
        Assert.assertEquals(responses.get(1).getBody(), HTTP_OK, responses.get(1).getStatus());

        // 50 - 51
        Assert.assertEquals(responses.get(2).getBody(), HTTP_BAD_REQUEST, responses.get(2).getStatus());

        // 50 - 50
        Assert.assertEquals(responses.get(3).getBody(), HTTP_OK, responses.get(3).getStatus());

        // 0 - 1
        Assert.assertEquals(responses.get(4).getBody(), HTTP_BAD_REQUEST, responses.get(4).getStatus());

        Assert.assertEquals(HTTP_OK, responses.get(5).getStatus());
        Assert.assertEquals("0", responses.get(5).getBody());
    }

    @Test
    public void transferValidTest() throws UnirestException {
        createAccount(5, 200);
        createAccount(6, 0);

        HttpResponse<String> response = transfer(5, 6, 100);

        Assert.assertEquals(HTTP_OK, response.getStatus());
        Assert.assertEquals("100", getBalance(5).getBody());
        Assert.assertEquals("100", getBalance(6).getBody());
    }
}
