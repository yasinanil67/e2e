package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import pojos.RoomPojo;

import java.sql.*;

import static org.junit.Assert.assertEquals;
import static stepdefinitions.MedunnaRoomStepDefinitions.roomId;
import static stepdefinitions.MedunnaRoomStepDefinitions.roomNumberFaker;

public class DataBaseRoomStepDefinitions {
    Connection connection;
    Statement statement;

    @Given("connect to database")
    public void connect_to_database() throws SQLException {

        //1. Adım: Connection oluştur
        connection = DriverManager.getConnection("jdbc:postgresql://medunna.com:5432/medunna_db_v2", "select_user", "Medunna_pass_@6");
        //2. Adım Statement oluştur.
        statement = connection.createStatement();

    }

    @Then("read room and validate")
    public void read_room_and_validate() throws SQLException {
        RoomPojo expectedData = new RoomPojo(roomNumberFaker,"PREMIUM_DELUXE",true,123.00,"Created For End To End Test");
        //3. Adım: Query çalıştır.

        String query= "SELECT * FROM room WHERE id ="+roomId;//roomId --> UI testten geliyor

        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();
        assertEquals(expectedData.getRoomNumber(), resultSet.getObject("room_number"));
        assertEquals(expectedData.getRoomType(), resultSet.getObject("room_type"));
        assertEquals(expectedData.getStatus(), resultSet.getObject("status"));
        assertEquals(expectedData.getPrice()+"0", resultSet.getObject("price")+"");
        assertEquals(expectedData.getDescription(), resultSet.getObject("description"));

    }
}
