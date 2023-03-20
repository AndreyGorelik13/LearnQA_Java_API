package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

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

    @Test
    @Epic("Регистрация")
    @DisplayName("Создание пользователя с некорректным email(отсутствует @)")
    @Severity(SeverityLevel.BLOCKER)
    public void testCreateUserWithInvalidEmail () {

        String email = "vinkotovexample.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .createUserWithInvalidEmail("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @Epic("Регистрация")
    @ParameterizedTest
    @CsvSource({
            "testuser,testuser,testuser,testuser@gmail.com,",
            "testuser,testuser,testuser,,1313",
            "testuser,testuser,,testuser@gmail.com,1313",
            "testuser,,testuser,testuser@gmail.com,1313",
            ",testuser,testuser,testuser@gmail.com,1313",
    })
    @DisplayName("Создание пользователя без указания одного из полей")
    @Severity(SeverityLevel.BLOCKER)
    public void testCreateUserWithoutOneField (String username, String firstName, String lastName, String email, String password) {

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("email", email);
        userData.put("password", password);

        Response responseWithoutOneField = apiCoreRequests
                .createUserWithoutOneField("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseWithoutOneField, 400);
        Assertions.assertResponseHasPart(responseWithoutOneField, "The following required params are missed: ");
    }

    @Test
    @Epic("Регистрация")
    @DisplayName("Создание пользователя с очень коротким именем в один символ")
    @Severity(SeverityLevel.BLOCKER)
    public void testWithShortUserName () {
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);
        userData.put("username", "x");

        Response responseWithShortUsername = apiCoreRequests
                .createUserWithShortUserName("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseWithShortUsername, 400);
        Assertions.assertResponseTextEquals(responseWithShortUsername, "The value of 'username' field is too short");
    }

    @Test
    @Epic("Регистрация")
    @DisplayName("Создание пользователя с очень длинным именем - длиннее 250 символов")
    @Severity(SeverityLevel.BLOCKER)
    public void testWithLongUserName () {
        Map <String, String> userData = new HashMap<>();
        String longUsername = "ClarenceBennettChristopherKingPhillipJacksonPaulGreeneMarkRussellWalterBaileyMarcusHillJacobWilsonMarkAndrewsAndrewMillerMarkTateHarveyTaylorEdwardJacobsMichaelWalkerDanieGriffinJamesThompsonAlanStephensFrankAndersonAndrewDavisPaulCooperKennethDavisAnthonyThomasDavidFreemanEdwardMatthewsMatthewSimmons";
        userData = DataGenerator.getRegistrationData(userData);
        userData.put("username", longUsername);

        Response responseWithLongUsername = apiCoreRequests
                .testWithLongUserName("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseWithLongUsername, 400);
        Assertions.assertResponseTextEquals(responseWithLongUsername, "The value of 'username' field is too long");
    }
}