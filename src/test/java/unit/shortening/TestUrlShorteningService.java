package unit.shortening;

import app.config.ConfigShortening;
import app.services.shortening.ShorteningService;
import app.services.shortening.exceptions.ShortenedUrlNotFoundException;
import app.services.shortening.models.ShortenedUrlDaoImpl;
import com.mongodb.async.client.MongoDatabase;
import org.bson.Document;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import util.MongoRule;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.IntStream;

public class TestUrlShorteningService {
    @ClassRule
    public static final MongoRule mongoRule = new MongoRule();

    @Test
    public void testUrlShorteningServiceShortResolve() throws MalformedURLException {
        final String LONG_URL = "http://www.cisco.com/api?q=akfvsldnvjknskdnfvds";
        final String BASE_URL = "http://goo.gl";
        final String DBNAME = "test";
        final int HASH_LEN = 6;

        //Setup
        MongoDatabase db = mongoRule.getMongo().getDatabase(DBNAME);
        URL longUrl = new URL(LONG_URL);
        ShorteningService ss = new ShorteningService(db, new ConfigShortening() {
            @Override
            public URL baseUrl() {
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
        Assert.assertEquals(longUrl, resolvedLongUrl);
    }

    @Test
    public void testUrlShorteningServiceResolveMissing() throws MalformedURLException {
        final String SHORT_URL = "http://goo.gl/akfvsld";
        final String BASE_URL = "http://goo.gl";
        final String DBNAME = "test";
        final int HASH_LEN = 6;

        //Setup
        MongoDatabase db = mongoRule.getMongo().getDatabase(DBNAME);
        ShorteningService ss = new ShorteningService(db, new ConfigShortening() {
            @Override
            public URL baseUrl() {
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
            Assert.assertTrue(e.getCause() instanceof ShortenedUrlNotFoundException);
        }
    }

    @Test
    @Ignore("The test is not consistent")
    public void testUrlShorteningServiceRetryOnCollision() throws MalformedURLException {
        final String LONG_URL = "http://www.cisco.com/api?q=akfvsldnvjknskdnfvds";
        final String BASE_URL = "http://goo.gl";
        final String DBNAME = "test_retry";
        final int HASH_LEN = 1;

        //Setup
        MongoDatabase db = mongoRule.getMongo().getDatabase(DBNAME);
        URL longUrl = new URL(LONG_URL);
        ShorteningService ss = new ShorteningService(db, new ConfigShortening() {
            @Override
            public URL baseUrl() {
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

        //Try to insert 4 urls 10 times with HASH_LEN = 1
        IntStream.range(0, 10).forEach(i -> {

            IntStream.range(0, 4).forEach(j -> {
                ss.shortenUrl(longUrl).join();
            });
            //Clean the collection
            CompletableFuture<Void> cf = new CompletableFuture<>();
            db.getCollection(ShortenedUrlDaoImpl.SHORTENED_URL_COLLECTION_NAME).deleteMany(new Document(), (aVoid, t) -> {
                if (t != null) cf.completeExceptionally(t);
                else cf.complete(null);
            });
            cf.join();
        });

    }

}
