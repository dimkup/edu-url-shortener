package app.services.shortening;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;


public class ShortenedUrlDaoImpl implements ShortenedUrlDao {
    private static Logger log = LoggerFactory.getLogger(ShortenedUrlDaoImpl.class);

    private  MongoCollection<ShortenedUrl> shortenedUrlCollection;

    public ShortenedUrlDaoImpl( MongoDatabase database) {
        shortenedUrlCollection = database.getCollection("urls",ShortenedUrl.class);

        //Create index on shortUrl field if does not exist
        shortenedUrlCollection.createIndex(Indexes.text("shortUrl"), (s, t) -> {
            if (t!=null) {
                log.error("Can't create index for url collection",t);
            }
        });
    }

    @Override
    public void getShortenedUrlByShortUrlAsyc(String shortUrl, BiConsumer<ShortenedUrl, Throwable> callback) {
        shortenedUrlCollection.find(eq("shortUrl",shortUrl)).first((url,t)->{
            if (callback!=null) callback.accept(url,t);
        });
    }

    @Override
    public void createShortenedUrlAsync(ShortenedUrl url, Consumer<Throwable> callback) {
        shortenedUrlCollection.insertOne(url, (aVoid, t) -> {
            if (callback!=null) callback.accept(t);
        });
    }
}
