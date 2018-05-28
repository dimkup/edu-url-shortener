package integration.shortening;

import app.Application;
import app.api.v1.url.CreateShortenedUrlRequest;
import app.api.v1.url.CreateShortenedUrlResponse;
import io.restassured.RestAssured;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import util.ConfigRule;
import util.MongoRule;

import java.net.URL;
import java.security.NoSuchAlgorithmException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestShortenUrl {
    @ClassRule
    public static final MongoRule mongoRule = new MongoRule();
    @ClassRule
    public static final ConfigRule configRule = new ConfigRule();

    @Test
    public void testShortenUrl() throws NoSuchAlgorithmException {
        final String LONG_URL = "http://www.cisco.com/api?q=akfvsldnvjknskdnfvds";

        Application app = new Application(configRule.getConfig());

        app.start();

        String baseUrl = configRule.getConfig().shortening().baseUrl().toString();
        RestAssured.baseURI = baseUrl;

        try {
            CreateShortenedUrlResponse response = given()
                    .contentType("application/json")
                    .body(new CreateShortenedUrlRequest(LONG_URL))
                    .when()
                    .post("/api/v1/url").as(CreateShortenedUrlResponse.class);
            Assert.assertEquals(LONG_URL,response.getLongUrl());
            Assert.assertTrue(response.getShortUrl().startsWith(baseUrl));

            given()
                    .contentType("application/json")
                    .when()
                    .redirects().follow(false)
                    .get(response.getShortUrl())
                    .then()
                    .body(equalTo(""))
                    .statusCode(302)
                    .header("Location",LONG_URL);

        } finally {
            app.stop();
        }


    }
}
