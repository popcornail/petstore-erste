package com.example.sandbox.postNewPet;

import com.example.sandbox.Common;
import com.example.sandbox.util.body.pet.PostCreatePet;
import com.example.sandbox.util.swagger.definitions.Item;
import com.example.sandbox.util.swagger.definitions.PetBody;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.report.TestListener;

import static com.example.sandbox.util.Tools.generateRandomNumber;
import static com.example.sandbox.util.body.pet.JsonBody.createJsonBody;
import static com.example.sandbox.util.constans.Tags.SMOKE;
import static com.example.sandbox.util.constans.TestData.HYDRAIMAGE;

@Listeners(TestListener.class)
public class postNewPetTest extends Common {

    private static final Logger log = LogManager.getLogger(postNewPetTest.class);

    @Test(enabled = true, groups = {SMOKE}, description = "Positive - create pet and validate response")
    public void testPostNewPetPositive(){
        PostCreatePet body = PostCreatePet.builder()
                .PetBody(PetBody.builder()
                        .id(generateRandomNumber())
                        .category(Item.builder()
                                .id(1)
                                .name("Hydra")
                                .build())
                        .name("Princess")
                        .photoUrl(HYDRAIMAGE)
                        .tag(Item.builder()
                                .id(2)
                                .name("cute")
                                .build())
                        .status("available")
                        .build()
                ).build();

        Response response = postUrl(newPet, createJsonBody(body));

        Assert.assertEquals(response.getStatusCode(), 200, "Invalid response code");

        if (response.getTime() > 500) {
            log.warn("Response time exceeded 500ms: " + response.getTime() + "ms");
        }

        Assert.assertNotNull(response.jsonPath().get("id"), "Response should contain 'id'");
        Assert.assertNotNull(response.jsonPath().get("name"), "Response should contain 'name'");
        Assert.assertEquals(response.jsonPath().get("name"), "Princess", "Pet name mismatch");
        Assert.assertEquals(response.jsonPath().get("status"), "available", "Pet status mismatch");
    }

    @Test(enabled = true, groups = {SMOKE}, description = "Negative - create pet with invalid body")
    public void testPostNewPetNegative(){
        Response response = postUrl(newPet, "invalid_body");

        //Does not give correct response based on the documentation
        Assert.assertEquals(response.getStatusCode(), 405, "Expected 405 for invalid input");

        if (response.getTime() > 500) {
            log.warn("Response time exceeded 500ms: " + response.getTime() + "ms");
        }
    }
}
