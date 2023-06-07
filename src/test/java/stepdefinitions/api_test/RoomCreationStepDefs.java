package stepdefinitions.api_test;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import pojos.RoomPojo;

import java.util.HashMap;
import java.util.Map;

import static base_urls.MedunnaBaseUrl.spec;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class RoomCreationStepDefs {
    Response response;
    int fakeRoomNumber;
    Map<String, Object> expectedDataMap;
    RoomPojo expectedDataPojo;

    @Given("send post request for creating room")
    public void send_post_request_for_creating_room() {
        //Set the url
        spec.pathParams("first", "api", "second", "rooms");
        fakeRoomNumber = Faker.instance().number().numberBetween(100000, 1000000);

        //Set the expected data
        //1. Yol: String --> Tavsiye edilmez
        String expectedDataString = "{ \"description\": \"Created By API\",  \"price\": 200, \"roomNumber\": " + fakeRoomNumber + ", \"roomType\": \"TWIN\", \"status\": true}";

        //2. Yol: Map
        expectedDataMap = new HashMap<>();
        expectedDataMap.put("description", "Created By API");
        expectedDataMap.put("price", 200);
        expectedDataMap.put("roomNumber", fakeRoomNumber);
        expectedDataMap.put("roomType", "TWIN");
        expectedDataMap.put("status", true);

        //3. Yol: Pojo --> Tavsiye edilen yöntem
        expectedDataPojo = new RoomPojo(fakeRoomNumber, "TWIN", true, 200.0, "Created By API");

        //Send the request and get the response
        response = given(spec).body(expectedDataPojo).post("{first}/{second}");
        response.prettyPrint();

    }

    @Then("get the response and validate")
    public void get_the_response_and_validate() throws JsonProcessingException {
        //Do assertion
        //1. Yol: then() methodu ile alınan datayı hamcrestmatcher ile doğrulama
        response
                .then()
                .statusCode(201)
                .body("roomNumber", equalTo(fakeRoomNumber),
                        "roomType", equalTo("TWIN"),
                        "status", equalTo(true),
                        "price", equalTo(200.0F),
                        "description", equalTo("Created By API"));

        //2. Yol: JsonPath ile datayı Java objesi olarak alıp doğrulama
        JsonPath jsonPath = response.jsonPath();
        assertEquals(fakeRoomNumber, jsonPath.getInt("roomNumber"));
        assertEquals("TWIN", jsonPath.getString("roomType"));
        assertTrue(jsonPath.getBoolean("status"));
        assertEquals(200.0 + "", jsonPath.getDouble("price") + "");//Data dönüşümü problemi nedeniyle iki datayı String yaptık
        assertEquals("Created By API", jsonPath.getString("description"));

        //3. Yol: Map ile
        Map<String, Object> actualDataMap = response.as(HashMap.class);
        assertEquals(201, response.statusCode());
        assertEquals(expectedDataMap.get("roomNumber") + ".0", actualDataMap.get("roomNumber") + "");
        assertEquals(expectedDataMap.get("roomType"), actualDataMap.get("roomType"));
        assertEquals(expectedDataMap.get("status"), actualDataMap.get("status"));
        assertEquals(expectedDataMap.get("price") + ".0", actualDataMap.get("price") + "");
        assertEquals(expectedDataMap.get("description"), actualDataMap.get("description"));

        //4. Yol: Pojo Class ile
        RoomPojo actualDataPojo = response.as(RoomPojo.class);

        assertEquals(expectedDataPojo.getRoomNumber(), actualDataPojo.getRoomNumber());
        assertEquals(expectedDataPojo.getRoomType(), actualDataPojo.getRoomType());
        assertEquals(expectedDataPojo.getStatus(), actualDataPojo.getStatus());
        assertEquals(expectedDataPojo.getPrice(), actualDataPojo.getPrice());
        assertEquals(expectedDataPojo.getDescription(), actualDataPojo.getDescription());

        //5. Yol: Object Mapper + Pojo --> Tavsiye edilen
        RoomPojo actualDataPojoMapper = new ObjectMapper().readValue(response.asString(), RoomPojo.class);

        assertEquals(expectedDataPojo.getRoomNumber(), actualDataPojoMapper.getRoomNumber());
        assertEquals(expectedDataPojo.getRoomType(), actualDataPojoMapper.getRoomType());
        assertEquals(expectedDataPojo.getStatus(), actualDataPojoMapper.getStatus());
        assertEquals(expectedDataPojo.getPrice(), actualDataPojoMapper.getPrice());
        assertEquals(expectedDataPojo.getDescription(), actualDataPojoMapper.getDescription());

        //6. Yol: Gson + Pojo
        RoomPojo actualDataPojoGson = new Gson().fromJson(response.asString(), RoomPojo.class);

        assertEquals(expectedDataPojo.getRoomNumber(), actualDataPojoGson.getRoomNumber());
        assertEquals(expectedDataPojo.getRoomType(), actualDataPojoGson.getRoomType());
        assertEquals(expectedDataPojo.getStatus(), actualDataPojoGson.getStatus());
        assertEquals(expectedDataPojo.getPrice(), actualDataPojoGson.getPrice());
        assertEquals(expectedDataPojo.getDescription(), actualDataPojoGson.getDescription());

    }
}
