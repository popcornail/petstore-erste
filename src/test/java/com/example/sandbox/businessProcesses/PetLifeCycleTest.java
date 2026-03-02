package com.example.sandbox.businessProcesses;

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
public class PetLifeCycleTest extends Common {

    private static final Logger log = LogManager.getLogger(PetLifeCycleTest.class);

    @Test(enabled = true, groups = {SMOKE}, description = "Pet lifecycle - create, update, getById, delete, getById")
    public void testPetLifeCycle(){
        int petId = generateRandomNumber();

        PostCreatePet createBody = PostCreatePet.builder()
                .PetBody(PetBody.builder()
                        .id(petId)
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

        Response createResponse = postUrl(newPet, createJsonBody(createBody));
        Assert.assertEquals(createResponse.getStatusCode(), 200, "Create - invalid response code");
        if (createResponse.getTime() > 500) {
            log.warn("Create response time exceeded 500ms: " + createResponse.getTime() + "ms");
        }

        PostCreatePet updateBody = PostCreatePet.builder()
                .PetBody(PetBody.builder()
                        .id(petId)
                        .category(Item.builder()
                                .id(1)
                                .name("Hydra")
                                .build())
                        .name("PrincessUpdated")
                        .photoUrl(HYDRAIMAGE)
                        .tag(Item.builder()
                                .id(2)
                                .name("cute")
                                .build())
                        .status("pending")
                        .build()
                ).build();

        Response updateResponse = putUrl(newPet, createJsonBody(updateBody));
        Assert.assertEquals(updateResponse.getStatusCode(), 200, "Update - invalid response code");
        Assert.assertEquals(updateResponse.jsonPath().get("name"), "PrincessUpdated", "Update - name mismatch");
        Assert.assertEquals(updateResponse.jsonPath().get("status"), "pending", "Update - status mismatch");
        if (updateResponse.getTime() > 500) {
            log.warn("Update response time exceeded 500ms: " + updateResponse.getTime() + "ms");
        }

        Response getAfterUpdateResponse = getUrl(petById.replace("{petId}", String.valueOf(petId)));
        Assert.assertEquals(getAfterUpdateResponse.getStatusCode(), 200, "GetById after update - invalid response code");
        Assert.assertEquals(getAfterUpdateResponse.jsonPath().get("name"), "PrincessUpdated", "GetById after update - name mismatch");
        Assert.assertEquals(getAfterUpdateResponse.jsonPath().get("status"), "pending", "GetById after update - status mismatch");
        if (getAfterUpdateResponse.getTime() > 500) {
            log.warn("GetById after update response time exceeded 500ms: " + getAfterUpdateResponse.getTime() + "ms");
        }

        Response deleteResponse = deleteUrl(petById.replace("{petId}", String.valueOf(petId)));
        Assert.assertEquals(deleteResponse.getStatusCode(), 200, "Delete - invalid response code");
        if (deleteResponse.getTime() > 500) {
            log.warn("Delete response time exceeded 500ms: " + deleteResponse.getTime() + "ms");
        }

        Response getAfterDeleteResponse = getUrl(petById.replace("{petId}", String.valueOf(petId)));
        Assert.assertEquals(getAfterDeleteResponse.getStatusCode(), 404, "GetById after delete - pet should not exist");
        if (getAfterDeleteResponse.getTime() > 500) {
            log.warn("GetById after delete response time exceeded 500ms: " + getAfterDeleteResponse.getTime() + "ms");
        }
    }
}
