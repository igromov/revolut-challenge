package me.igromov.exchanger;

import kong.unirest.HttpResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TransferIT extends BaseIT {

    @Test
    public void transferNonExistentAccountsTest() {
        HttpResponse<String> response = transfer(1, 2, 100);
        Assert.assertEquals(response.getBody(), 400, response.getStatus());

        createAccount(1, 100);
        HttpResponse<String> response1 = transfer(1, 2, 100);
        Assert.assertEquals(response1.getBody(), 400, response1.getStatus());

        createAccount(2, 100);
        HttpResponse<String> response2 = transfer(1, 2, 100);
        Assert.assertEquals(response2.getBody(), 200, response2.getStatus());
    }

    @Test
    public void transferInvalidAmounts() {
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
        Assert.assertEquals(responses.get(0).getBody(), 400, responses.get(0).getStatus());

        // 200 - 150
        Assert.assertEquals(responses.get(1).getBody(), 200, responses.get(1).getStatus());

        // 50 - 51
        Assert.assertEquals(responses.get(2).getBody(), 400, responses.get(2).getStatus());

        // 50 - 50
        Assert.assertEquals(responses.get(3).getBody(), 200, responses.get(3).getStatus());

        // 0 - 1
        Assert.assertEquals(responses.get(4).getBody(), 400, responses.get(4).getStatus());

        Assert.assertEquals(200, responses.get(5).getStatus());
        Assert.assertEquals("0", responses.get(5).getBody());
    }
}
