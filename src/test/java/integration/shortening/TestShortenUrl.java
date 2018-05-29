package integration.shortening;

import app.Application;
import app.api.v1.url.CreateShortenedUrlRequest;
import app.api.v1.url.CreateShortenedUrlResponse;
import io.restassured.RestAssured;
import org.junit.*;
import util.ConfigRule;
import util.MongoRule;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestShortenUrl {
    @ClassRule
    public static final MongoRule mongoRule = new MongoRule();
    @ClassRule
    public static final ConfigRule configRule = new ConfigRule();


    @Before
    public void setUp() throws NoSuchAlgorithmException {
        this.app = new Application(configRule.getConfig());

        app.start();

        baseUrl = configRule.getConfig().shortening().baseUrl().toString();
        RestAssured.baseURI = baseUrl;

    }

    @After
    public void tearDown() {
        this.app.stop();
        this.app = null;
        baseUrl = null;
    }

    private Application app;
    private String baseUrl;

    @Test
    public void testServiceShouldShortenAndRedirect() {
        final String LONG_URL = "http://www.cisco.com/api?q=akfvsldnvjknskdnfvds";


        //Create
        CreateShortenedUrlResponse createResponse = given()
                .contentType("application/json")
                .body(new CreateShortenedUrlRequest(LONG_URL))
                .when()
                .post("/api/v1/url")
                .then()
                .assertThat().statusCode(201)
                .assertThat().extract().body()
                .as(CreateShortenedUrlResponse.class);

        Assert.assertEquals(LONG_URL, createResponse.getLongUrl());
        Assert.assertTrue(createResponse.getShortUrl().startsWith(baseUrl));

        //Get is back
        CreateShortenedUrlResponse getResponse = given()
                .contentType("application/json")
                .queryParam("shortUrl", createResponse.getShortUrl())
                .when()
                .get("/api/v1/url")
                .then()
                .assertThat().statusCode(200)
                .assertThat().extract().body()
                .as(CreateShortenedUrlResponse.class);
        Assert.assertEquals(LONG_URL, getResponse.getLongUrl());
        Assert.assertEquals(createResponse.getShortUrl(), getResponse.getShortUrl());

        //Redirect
        given()
                .contentType("application/json")
                .when()
                .redirects().follow(false)
                .get(createResponse.getShortUrl())
                .then()
                .body(equalTo(""))
                .statusCode(302)
                .header("Location", LONG_URL);


    }

    @Test
    public void testServiceShouldReturn404ForMissingShortUrls() throws MalformedURLException {
        URL missingShortUrl = new URL(baseUrl + "/abcd");

        //Ask for missing URL
        given()
                .contentType("application/json")
                .queryParam("shortUrl", missingShortUrl.toString())
                .when()
                .get("/api/v1/url").peek()
                .then()
                .assertThat().statusCode(404)
                .body("error", is("ShortUrl is not found"));

        //Redirect
        given()
                .contentType("application/json")
                .when()
                .redirects().follow(false)
                .get(missingShortUrl.toString())
                .then()
                .statusCode(404)
                .body("error", is("ShortUrl is not found"));
    }

    @Test
    public void testServiceShouldRetrun400ForBadLongUrl() {
        final String BAD_LONG_URL = "hakfvsldnvjknskdnfvds";
        final String BAD_SHORT_URL = "hakfv sldnv jkn";


        //Try Create
        given()
                .contentType("application/json")
                .body(new CreateShortenedUrlRequest(BAD_LONG_URL))
                .when()
                .post("/api/v1/url")
                .then()
                .assertThat().statusCode(400)
                .body("error", is("Can't parse the URL"));

        //Ask for missing URL
        given()
                .contentType("application/json")
                .queryParam("shortUrl", BAD_SHORT_URL)
                .when()
                .get("/api/v1/url")
                .then()
                .assertThat().statusCode(400)
                .body("error", is("Can't parse the URL"));

    }
}
