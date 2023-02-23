import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class HelloFromAndrey {

    @Test
    public void testHelloFromAndrey(){
        System.out.println("Hello from Andrey");
    }

    @Test
    public void testGetTextApi(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }
}