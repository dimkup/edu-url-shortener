package app.services.shortening.models;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

/**
 * Implementation of the ShortenedUrlDao
 */
public class ShortenedUrlDaoImpl implements ShortenedUrlDao {
    public static final String SHORTENED_URL_COLLECTION_NAME = "urls"; //Collection name

    private static Logger log = LoggerFactory.getLogger(ShortenedUrlDaoImpl.class);

    private  MongoCollection<ShortenedUrl> shortenedUrlCollection;

    public ShortenedUrlDaoImpl( MongoDatabase database) {
        shortenedUrlCollection = database.getCollection(SHORTENED_URL_COLLECTION_NAME,ShortenedUrl.class);

        //Create unique index on shortUrl field if does not exist
        IndexOptions indexOptions = new IndexOptions().unique(true);
        shortenedUrlCollection.createIndex(
                Indexes.ascending("shortUrl"),
                indexOptions,
                (s, t) -> {if (t!=null) log.error("Can't create index for url collection",t);});
    }


    @Override
    public CompletableFuture<ShortenedUrl> getShortenedUrlByShortUrlAsyc(String shortUrl) {
        CompletableFuture<ShortenedUrl> cf = new CompletableFuture<>();

        shortenedUrlCollection.find(eq("shortUrl",shortUrl)).first((url,t)->{
            if (t!=null) cf.completeExceptionally(t);
            else cf.complete(url);
        });
        return cf;
    }

    @Override
    public CompletableFuture<Void> createShortenedUrlAsync(ShortenedUrl url) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        shortenedUrlCollection.insertOne(url, (aVoid, t) -> {
            if (t!=null) cf.completeExceptionally(t);
            else cf.complete(null);
        });
        return cf;
    }
}
