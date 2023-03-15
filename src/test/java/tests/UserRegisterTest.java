package tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    @Test
    public void testCreateUserWithExistEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userDate = new HashMap<>();
        userDate.put("email", email);
        userDate = DataGenerator.getRegistrationData(userDate);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userDate)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Users with email '" + email + "' already exists");
    }

    @Test
    @Description("This test checks positive case of registration")
    @DisplayName("Test positive user registration")
    public void testCreateUserSuccessfully(){

        Map<String,String> userData =  DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/ajax/api/user")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }
}