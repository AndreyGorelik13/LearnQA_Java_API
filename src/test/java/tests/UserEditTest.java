package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testEditJustCreatedUser(){
        // GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .jsonPath();
        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    public void testEditDataWithoutAuth () {

        String newName = "Changed Name";
        Map <String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/65614", editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }

    @Test
    public void testChangeEmailOfUser () {
        //LOGIN
        Map <String, String> authData = new HashMap<>();
        authData.put("email", "learnqa20230316012119@example.com");
        authData.put("password", "123");

        Response responseGetAuth = apiCoreRequests
                .changeEmailOfUser("https://playground.learnqa.ru/api/user/login", authData);

        String header = responseGetAuth.getHeader("x-csrf-token");
        String cookie = responseGetAuth.getCookie("auth_sid");

        //EDIT EMAIL USER
        String newEmail = "textemailgmail.com";
        Map <String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests
                .makePutRequestToChangeEmail("https://playground.learnqa.ru/api/user/65616", editData, header, cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/65616", header, cookie);

        Assertions.assertJsonByName(responseUserData, "email","learnqa20230316012119@example.com");
    }

    @Test
    public void testChangeFirstNameOfUser() {
        //LOGIN
        Map <String, String> authData = new HashMap<>();
        authData.put("email", "learnqa20230316012119@example.com");
        authData.put("password", "123");

        Response responseGetAuth = apiCoreRequests
                .changeFirstNameOfUser("https://playground.learnqa.ru/api/user/login", authData);

        String header = responseGetAuth.getHeader("x-csrf-token");
        String cookie = responseGetAuth.getCookie("auth_sid");

        //EDIT FIRST NAME USER
        String newFirstName = "x";
        Map <String, String> editData = new HashMap<>();
        editData.put("firstName", newFirstName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestToChangeFirstName("https://playground.learnqa.ru/api/user/65616", editData, header, cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/65616", header, cookie);

        Assertions.assertJsonByName(responseUserData, "firstName","Changed Name");
    }

    @Test
    public void testEditDataByAuthOtherUser () {
        //LOGIN
        Map <String, String> authData = new HashMap<>();
        authData.put("email", "learnqa20230316012119@example.com");
        authData.put("password", "123");

        Response responseGetAuth = apiCoreRequests
                .changeFirstNameOfUser("https://playground.learnqa.ru/api/user/login", authData);

        String header = responseGetAuth.getHeader("x-csrf-token");
        String cookie = responseGetAuth.getCookie("auth_sid");

        //EDIT DATA OF ANOTHER USER
        String newName = "Changed Name";
        Map <String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestToChangeDataOfAnotherUser("https://playground.learnqa.ru/api/user/65614", editData, header, cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
    }
}