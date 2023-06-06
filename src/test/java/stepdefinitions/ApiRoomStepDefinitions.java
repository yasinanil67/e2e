package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static base_urls.MedunnaBaseUrl.spec;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static stepdefinitions.MedunnaRoomStepDefinitions.roomNumberFaker;

public class ApiRoomStepDefinitions {
    Response response;

    @Given("send get request to url")
    public void send_get_request_to_url() {
        //Set the url--> https://medunna.com/api/rooms?sort=createdDate,desc
        spec.pathParams("first","api","second","rooms")
                .queryParams("sort","createdDate,desc");

        //Set the expected data


        //Send the request and get the response
        response = given(spec).get("{first}/{second}");
        response.prettyPrint();

    }
    @When("validate body")
    public void validate_body() {
        //Do assertion
        Object actualRoomType = response.jsonPath().getList("findAll{it.roomNumber=="+roomNumberFaker+"}.roomType").get(0);//Gelen body içinden bizim oluşturduğumuz odanın numarası ile filtreleme yapıyoruz.
        Object actualStatus = response.jsonPath().getList("findAll{it.roomNumber=="+roomNumberFaker+"}.status").get(0);//Filtrelenen bodyden gerekli datayı nokta sonrasına belirterek alıyoruz.
        Object actualPrice = response.jsonPath().getList("findAll{it.roomNumber=="+roomNumberFaker+"}.price").get(0);
        Object actualDescription = response.jsonPath().getList("findAll{it.roomNumber=="+roomNumberFaker+"}.description").get(0);
        Object actualRoomNumber = response.jsonPath().getList("findAll{it.roomNumber=="+roomNumberFaker+"}.roomNumber").get(0);
        System.out.println("roomNumberFaker = " + roomNumberFaker);

        assertEquals("PREMIUM_DELUXE", actualRoomType);
        assertEquals(true, actualStatus);
        assertEquals( "123.0", actualPrice+"");
        assertEquals( "Created For End To End Test", actualDescription);
        assertEquals( roomNumberFaker, actualRoomNumber);

    }
}
