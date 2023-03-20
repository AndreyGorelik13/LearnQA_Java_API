package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    @Step("Make a GET-request with token and auth cookie")
    public Response makeGetRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithToken(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithCookie(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request")
    public Response makePostRequest(String url, Map<String,String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Create user with invalid email - without @")
    public Response createUserWithInvalidEmail (String url, Map<String, String> userData ) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step ("Create user without one parameter")
    public Response createUserWithoutOneField (String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step ("Create user with too short username")
    public Response createUserWithShortUserName(String url, Map<String, String> userData ) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Create user with too long username")
    public Response testWithLongUserName(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Login and check details of another user")
    public Response testGetUserDetailsAuthAsOtherUser (String url, Map<String, String> authData, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .body(authData)
                .get(url)
                .andReturn();
    }

    @Step("Change data by unauthorized user")
    public Response makePutRequest(String url, Map<String, String> editData) {
        return given()
                .filter(new AllureRestAssured())
                .body(editData)
                .put(url)
                .andReturn();
    }

    @Step ("Change email of user (POST)")
    public Response changeEmailOfUser (String url,Map<String, String> userData ) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step ("Change firstName of user")
    public Response changeFirstNameOfUser (String url,Map<String, String> userData ) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Change email of user (PUT)")
    public Response makePutRequestToChangeEmail (String url, Map<String, String> authData, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .body(authData)
                .put(url)
                .andReturn();
    }

    @Step("Change firstName of user")
    public Response makePutRequestToChangeFirstName (String url, Map<String, String> authData, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .body(authData)
                .put(url)
                .andReturn();
    }

    @Step("Change data of another user")
    public Response makePutRequestToChangeDataOfAnotherUser (String url, Map<String, String> authData, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .body(authData)
                .put(url)
                .andReturn();
    }

    @Step("Try to delete user with id 2")
    public Response makeDeleteRequest (String url, Map<String, String> userData, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .body(userData)
                .delete(url)
                .andReturn();
    }
}