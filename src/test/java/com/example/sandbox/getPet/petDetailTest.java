package com.example.sandbox.getPet;

import com.example.sandbox.Common;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.report.TestListener;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.example.sandbox.util.constans.Tags.SMOKE;

@Listeners(TestListener.class)
public class petDetailTest extends Common {

    private static final Logger log = LogManager.getLogger(petDetailTest.class);

    @Test(enabled = true, groups = {SMOKE}, description = "description")
    public void Test1(){
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put("status","available");

        Response response = getUrl(findByStatus, queryParams);
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        String id = response.jsonPath().get("find{it.status.equals('available')}.id").toString();

        Response response2 = getUrl(petById.replace("{petId}",id));
        Assert.assertEquals(response2.getStatusCode(),200,"Invalid response code");
    }

    @Test(enabled = true, groups = {SMOKE}, description = "Positive - get pet by id")
    public void testGetPetByIdPositive(){
        Response response = getUrl(petById.replace("{petId}", "1"));

        Assert.assertEquals(response.getStatusCode(), 200, "Invalid response code");

        if (response.getTime() > 500) {
            log.warn("Response time exceeded 500ms: " + response.getTime() + "ms");
        }

        Assert.assertNotNull(response.jsonPath().get("id"), "Required field 'id' is missing");
        Assert.assertNotNull(response.jsonPath().get("name"), "Required field 'name' is missing");
        Assert.assertNotNull(response.jsonPath().get("photoUrls"), "Required field 'photoUrls' is missing");

        Object id = response.jsonPath().get("id");
        Assert.assertTrue(id instanceof Integer || id instanceof Long,
                "Field 'id' should be numeric, but was: " + id.getClass().getSimpleName());

        Object name = response.jsonPath().get("name");
        Assert.assertTrue(name instanceof String,
                "Field 'name' should be string, but was: " + name.getClass().getSimpleName());

        Object photoUrls = response.jsonPath().get("photoUrls");
        Assert.assertTrue(photoUrls instanceof List,
                "Field 'photoUrls' should be array, but was: " + photoUrls.getClass().getSimpleName());

        String status = response.jsonPath().get("status");
        if (status != null) {
            Assert.assertTrue(
                    status.equals("available") || status.equals("pending") || status.equals("sold"),
                    "Status must be one of: available, pending, sold. Got: " + status
            );
        }
    }

    @Test(enabled = true, groups = {SMOKE}, description = "Negative - create pet with invalid body")
    public void testPostNewPetNegative(){
        Response response = postUrl(newPet, "invalid_body");

/* TODO: BUG - According to Swagger documentation, expected response is 405 Invalid Input,
         but the API returns 400 Bad Request instead.
         Swagger: https://petstore.swagger.io/#/pet/addPet -> Response 405: Invalid input
         Actual response: 400 Bad Request {"code":400,"type":"unknown","message":"bad input"} */

        Assert.assertEquals(response.getStatusCode(), 400, "Expected 405 per documentation, actual API returns 400");


        if (response.getTime() > 500) {
            log.warn("Response time exceeded 500ms: " + response.getTime() + "ms");
        }
    }

}
