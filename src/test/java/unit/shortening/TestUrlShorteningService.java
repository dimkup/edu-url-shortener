package unit.shortening;

import app.config.ConfigShortening;
import app.services.shortening.ShortUrlConstructor;
import app.services.shortening.ShorteningService;
import app.services.shortening.exceptions.ShortenedUrlNotFoundException;
import com.mongodb.async.client.MongoDatabase;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import util.MongoRule;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletionException;

public class TestUrlShorteningService {
    @ClassRule
    public static final MongoRule mongoRule = new MongoRule();

    @Test
    public void testUrlShorteningServiceShortResolve() throws NoSuchAlgorithmException, MalformedURLException {
        final String LONG_URL = "http://www.cisco.com/api?q=akfvsldnvjknskdnfvds";
        final String BASE_URL = "http://goo.gl";
        final String DBNAME = "test";
        final int HASH_LEN = 6;

        //Setup
        MongoDatabase db = mongoRule.getMongo().getDatabase(DBNAME);
        URL longUrl = new URL(LONG_URL);
        ShorteningService ss = new ShorteningService(db, new ConfigShortening() {
            @Override
            public URL baseUrl()  {
                try {
                    return new URL(BASE_URL);
                } catch (MalformedURLException e) {
                    Assert.fail("Can't construct URL");
                }
                return null;
            }

            @Override
            public Integer hashLen() {
                return HASH_LEN;
            }
        });

        //Short
        URL shortUrl = ss.shortenUrl(longUrl).join();

        //Resolve
        URL resolvedLongUrl = ss.resolveUrl(shortUrl).join();

        //Check
        Assert.assertEquals(longUrl,resolvedLongUrl);
    }

    @Test
    public void testUrlShorteningServiceResolveMissing() throws NoSuchAlgorithmException, MalformedURLException {
        final String SHORT_URL = "http://goo.gl/akfvsld";
        final String BASE_URL = "http://goo.gl";
        final String DBNAME = "test";
        final int HASH_LEN = 6;

        //Setup
        MongoDatabase db = mongoRule.getMongo().getDatabase(DBNAME);
        ShorteningService ss = new ShorteningService(db,new ConfigShortening() {
            @Override
            public URL baseUrl()  {
                try {
                    return new URL(BASE_URL);
                } catch (MalformedURLException e) {
                    Assert.fail("Can't construct URL");
                }
                return null;
            }

            @Override
            public Integer hashLen() {
                return HASH_LEN;
            }
        });
        URL shortUrl = new URL(SHORT_URL);

        //Resolve
        try {
            ss.resolveUrl(shortUrl).join();
            Assert.fail("Missing shortUrl has been resolved!");
        } catch (CompletionException e) {
            Assert.assertTrue( e.getCause() instanceof ShortenedUrlNotFoundException);
        }
    }

}
