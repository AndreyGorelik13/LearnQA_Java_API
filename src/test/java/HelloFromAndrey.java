import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelloFromAndrey {

    @Test
    public void testHelloFromAndrey() {
        System.out.println("Hello from Andrey");
    }

    @Test
    public void testGetTextApi() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testGetJsonHomework() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String secondMessage = response.get("messages[1].message");
        System.out.println(secondMessage);
    }

    @Test
    public void testGetLongRedirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    @Test
    public void testGetLongRedirectSearchEnd() {
        int status = 0;
        String location = "https://playground.learnqa.ru/api/long_redirect";
        while (status != 200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(location)
                    .andReturn();

            int statusCode = response.getStatusCode();
            String locationHeader = response.getHeader("Location");
            System.out.println(locationHeader);
            System.out.println(statusCode);
            location = locationHeader;
            status = statusCode;
        }
    }

    @Test
    public void testGetLongTimeJobToken() throws InterruptedException {
        JsonPath startJob = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String tokenValue = startJob.get("token");
        int timeoutSeconds = startJob.get("seconds");

        JsonPath checkResultWithoutTimeout = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job?token=" + tokenValue)
                .jsonPath();

        String statusWithoutTimeout = checkResultWithoutTimeout.get("status");
        assert Objects.equals(statusWithoutTimeout, "Job is NOT ready");

        Thread.sleep(timeoutSeconds * 1000L);

        JsonPath checkResultWithTimeout = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job?token=" + tokenValue)
                .jsonPath();

        String statusWithTimeout = checkResultWithTimeout.get("status");
        String resultJob = checkResultWithTimeout.get("result");
        assert Objects.equals(statusWithTimeout, "Job is ready");
        assert !Objects.equals(resultJob, "");
    }

    @Test
    public void testPostBruteForcePassword() {
        List<String> passwords = Arrays.asList("password", "123456", "123456789", "12345678", "12345", "qwerty", "abc123", "football", "1234567",
                "monkey", "111111", "letmein", "1234", "1234567890", "dragon", "baseball", "sunshine", "iloveyou", "trustno1", "princess", "1dragon",
                "adobe123[a]", "123123", "welcome", "login", "admin", "qwerty123", "solo", "1q2w3e4r", "master", "666666", "photoshop[a]", "1qaz2wsx",
                "qwertyuiop", "ashley", "mustang", "121212", "starwars", "654321", "bailey", "access", "flower", "555555", "passw0rd", "shadow", "lovely",
                "7777777", "michael", "!@#$%^&*", "jesus", "password1", "superman", "hello", "charlie", "888888", "696969", "hottie", "freedom", "aa123456",
                "qazwsx", "ninja", "azerty", "loveme", "whatever", "donald", "batman", "zaq1zaq1", "Football", "000000", "123qwe");

        for (String password : passwords) {

            Response checkPassword = RestAssured
                    .given()
                    .queryParam("login", "super_admin")
                    .queryParam("password", password)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String cookie = checkPassword.getCookie("auth_cookie");

            Map<String, String> mapCookies = new HashMap<>();
            mapCookies.put("auth_cookie", cookie);

            Response checkCookieAut = RestAssured
                    .given()
                    .cookies(mapCookies)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

           String serverResponse = checkCookieAut.asString();
           if (Objects.equals(serverResponse, "You are authorized")){
               System.out.println(password);
               System.out.println(serverResponse);
           }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"моделированный", "труднорастворимые"})
    public void testStringLengthCheck(String name){
        assertTrue(name.length() > 15, "The number of characters is less than 15. Name length = " + name.length());
    }

    @Test
    public void testAssertCookie(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String, String> allCookies = response.getCookies();
        String cookie = response.cookie("HomeWork");
        assertTrue(allCookies.containsKey("HomeWork"), "Response doesn't have 'HomeWork' cookie");
        assertEquals("hw_value", cookie, "Unexpected cookie");
    }

    @Test
    public void testAssertHeader(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        Headers allHeaders = response.headers();
        String headerValue = response.header("x-secret-homework-header");
        assertTrue(allHeaders.hasHeaderWithName("x-secret-homework-header"), "Response doesn't have 'x-secret-homework-header' header");
        assertEquals("Some secret value", headerValue, "Unexpected header");
    }
}