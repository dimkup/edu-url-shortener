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

import java.security.NoSuchAlgorithmException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

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
            //Create
            CreateShortenedUrlResponse createResponse = given()
                    .contentType("application/json")
                    .body(new CreateShortenedUrlRequest(LONG_URL))
                    .when()
                    .post("/api/v1/url").as(CreateShortenedUrlResponse.class);
            Assert.assertEquals(LONG_URL,createResponse.getLongUrl());
            Assert.assertTrue(createResponse.getShortUrl().startsWith(baseUrl));

            //Get is back
            CreateShortenedUrlResponse getResponse = given()
                    .contentType("application/json")
                    .queryParam("shortUrl",createResponse.getShortUrl())
                    .when()
                    .get("/api/v1/url").as(CreateShortenedUrlResponse.class);
            Assert.assertEquals(LONG_URL,getResponse.getLongUrl());
            Assert.assertEquals(createResponse.getShortUrl(),getResponse.getShortUrl());

            //Redirect
            given()
                    .contentType("application/json")
                    .when()
                    .redirects().follow(false)
                    .get(createResponse.getShortUrl())
                    .then()
                    .body(equalTo(""))
                    .statusCode(302)
                    .header("Location",LONG_URL);

        } finally {
            app.stop();
        }


    }
}
