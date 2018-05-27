package unit.shortening;

import app.services.shortening.ShortenedUrl;
import app.services.shortening.ShortenedUrlDao;
import app.services.shortening.ShortenedUrlDaoImpl;
import com.mongodb.MongoWriteException;
import com.mongodb.async.client.MongoDatabase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import util.MongoRule;

import java.util.concurrent.CompletionException;

public class TestShortenedUrlDao  {

    @Rule
    public final MongoRule mongoRule = new MongoRule();

    @Test
    public void testShortenedUrlDaoCreateRead() {
        final String LONG_URL = "http://www.cisco.com/api?q=akfvsldnvjknskdnfvds";
        final String SHORT_URL = "http://goo.gl/akfvsld";
        final String MISSING_SHORT_URL = "http://goo.gl/jcfqaks";
        final String DBNAME = "test";

        //Setup
        MongoDatabase db = mongoRule.getMongo().getDatabase(DBNAME);
        ShortenedUrlDao sud = new ShortenedUrlDaoImpl(db);

        //Create model in the database
        sud.createShortenedUrlAsync(new ShortenedUrl(SHORT_URL,LONG_URL)).join();

        //Get long URL by short URL
        Assert.assertEquals(LONG_URL,sud.getShortenedUrlByShortUrlAsyc(SHORT_URL).join().getLongUrl());

        //Read missing short URL
        Assert.assertNull(sud.getShortenedUrlByShortUrlAsyc(MISSING_SHORT_URL).join());

        //Drop db
        db.drop((aVoid,t)->{});
    }

    @Test
    public void testShortenedUrlDaoCreateExisting() {
        final String LONG_URL = "http://www.cisco.com/api?q=akfvsldnvjknskdnfvds";
        final String SHORT_URL = "http://goo.gl/akfvsld";
        final String DBNAME = "test";

        //Setup
        MongoDatabase db = mongoRule.getMongo().getDatabase(DBNAME);
        ShortenedUrlDao sud = new ShortenedUrlDaoImpl(db);

        //Create model in the database
        sud.createShortenedUrlAsync(new ShortenedUrl(SHORT_URL,LONG_URL)).join();

        //Recreate the same model in the database
        try {
            sud.createShortenedUrlAsync(new ShortenedUrl(SHORT_URL, LONG_URL)).join();
            Assert.fail("Non uniqe short url was inserted");
        } catch (CompletionException e) {
            Assert.assertTrue( e.getCause() instanceof MongoWriteException);
        }
        //Drop db
        db.drop((aVoid,t)->{});
    }
}
