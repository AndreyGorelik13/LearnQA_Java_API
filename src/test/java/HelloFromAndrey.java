import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.Objects;

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
}