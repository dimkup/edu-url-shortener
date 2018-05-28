package unit.shortening;

import app.services.shortening.models.ShortenedUrl;
import app.services.shortening.models.ShortenedUrlDao;
import app.services.shortening.models.ShortenedUrlDaoImpl;
import com.mongodb.MongoWriteException;
import com.mongodb.async.client.MongoDatabase;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import util.MongoRule;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class TestShortenedUrlDao  {

    @ClassRule
    public static final MongoRule mongoRule = new MongoRule();

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
        CompletableFuture<Void> cf = new CompletableFuture<>();
        db.drop((aVoid,t)->{if (t!=null) cf.completeExceptionally(t); else cf.complete(null);});
        cf.join();
    }

    @Test
    public void testShortenedUrlDaoCreateExisting() {
        final String LONG_URL = "http://www.cisco.com/api?q=akfvsldnvjknskdnfvds";
        final String SHORT_URL = "http://goo.gl/jjksdajf";
        final String DBNAME = "test";

        //Setup
        MongoDatabase db = mongoRule.getMongo().getDatabase(DBNAME);
        ShortenedUrlDao sud = new ShortenedUrlDaoImpl(db);

        //Create a model in the database
        sud.createShortenedUrlAsync(new ShortenedUrl(SHORT_URL,LONG_URL)).join();

        //Recreate the same model in the database
        try {
            sud.createShortenedUrlAsync(new ShortenedUrl(SHORT_URL, LONG_URL)).join();
            Assert.fail("Non unique short url was inserted");
        } catch (CompletionException e) {
            Assert.assertTrue( e.getCause() instanceof MongoWriteException);
        }
        //Drop db
        CompletableFuture<Void> cf = new CompletableFuture<>();
        db.drop((aVoid,t)->{if (t!=null) cf.completeExceptionally(t); else cf.complete(null);});
        cf.join();
    }
}
