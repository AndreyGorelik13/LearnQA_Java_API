package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Epic("Удаление пользователей")
    @DisplayName("Удаление пользователя по ID 2")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteUserId2() {

        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "1234");

        Response makeLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", userData);

        String cookie = makeLogin.getCookie("auth_sid");
        String token = makeLogin.getHeader("x-csrf-token");

        Response response = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/2", userData, token, cookie);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Epic("Удаление пользователей")
    @DisplayName("Удаление ранее созданного пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteOwnUser () {

        //CREATE NEW USER
        Map <String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map <String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //DELETE USER
        String cookie = responseGetAuth.getCookie("auth_sid");
        String token = responseGetAuth.getHeader("x-csrf-token");
        String url = "https://playground.learnqa.ru/api/user/" + userId;

        Response response = apiCoreRequests
                .makeDeleteRequest(url, userData, token, cookie);

        Assertions.assertResponseCodeEquals(response, 200);

        //GET USER BY ID
        Response responseAfterDeletion = apiCoreRequests
                .makeGetRequest(url, token, cookie);

        Assertions.assertResponseCodeEquals(responseAfterDeletion, 404);
        Assertions.assertResponseTextEquals(responseAfterDeletion, "User not found");
    }

    @Test
    @Epic("Удаление пользователей")
    @DisplayName("Удаление пользователя, авторизованным другим пользователем")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteUserIdByAnotherUser() {

        //LOGIN
        Map <String, String> userData = new HashMap<>();
        userData.put("email", "learnqa20230316012119@example.com");
        userData.put("password", "123");

        Response makeLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", userData);

        String cookie = makeLogin.getCookie("auth_sid");
        String token = makeLogin.getHeader("x-csrf-token");

        //TRY TO DELETE ANOTHER USER
        Response response = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/65614", userData, token, cookie);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Auth token not supplied");
    }
}