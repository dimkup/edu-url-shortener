package unit.shortening;

import app.services.shortening.ShortenedUrl;
import app.services.shortening.ShortenedUrlDao;
import app.services.shortening.ShortenedUrlDaoImpl;
import com.mongodb.async.client.MongoDatabase;
import org.junit.Assert;
import util.AbstractMongoDBTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TestShortenedUrlDao extends AbstractMongoDBTest {


    public void testShortenedUrlDaoCreate() {
        MongoDatabase db = getMongo().getDatabase("test");
        ShortenedUrlDao sud = new ShortenedUrlDaoImpl(db);
        final CompletableFuture cf_create = new CompletableFuture();
        sud.createShortenedUrlAsync(new ShortenedUrl("aaa","bbb"),(t)->{cf_create.complete(t);});
        cf_create.join();
        final CompletableFuture<ShortenedUrl> cf_get = new CompletableFuture();
        sud.getShortenedUrlByShortUrlAsyc("aaa",(url,t)->{
            if (t!=null) cf_get.completeExceptionally(t);
            else cf_get.complete(url);
        });
        Assert.assertEquals("bbb",cf_get.join().getLongUrl());

        final CompletableFuture<ShortenedUrl> cf_get_empty = new CompletableFuture();
        sud.getShortenedUrlByShortUrlAsyc("vvv",(url,t)->{
            if (t!=null) cf_get_empty.completeExceptionally(t);
            else cf_get_empty.complete(url);
        });

        try {
            Assert.assertNull(cf_get_empty.get());

        } catch (ExecutionException|InterruptedException e) {
            Assert.fail("serach fialed for the non existing url: "+e.getMessage());
        }
        db.drop((aVoid,t)->{});
    }
}
