package app.services.shortening;

import app.services.shortening.exceptions.ShortenedUrlNotFoundException;
import app.services.shortening.exceptions.ShorteningServiceException;
import app.services.shortening.models.ShortenedUrl;
import app.services.shortening.models.ShortenedUrlDao;
import app.services.shortening.models.ShortenedUrlDaoImpl;
import com.mongodb.async.client.MongoDatabase;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class ShorteningService {
    private ShortenedUrlDao shortenedUrlDao;
    private ShortUrlConstructor shortUrlConstructor;

    public ShorteningService(MongoDatabase database, ShortUrlConstructor constructor)  {
        this.shortenedUrlDao = new ShortenedUrlDaoImpl(database);
        this.shortUrlConstructor = constructor;
    }

    public CompletableFuture<URL> shortenUrl(URL longUrl) {
        URL shortUrl = shortUrlConstructor.generateShortUrl(longUrl);
        ShortenedUrl shortenedUrl = new ShortenedUrl(shortUrl.toString(),longUrl.toString());
        return shortenedUrlDao
                .createShortenedUrlAsync(shortenedUrl)
                .thenApply(v-> shortUrl);
    }

    public CompletableFuture<URL> resolveUrl(URL shortUrl) {
        return shortenedUrlDao
                .getShortenedUrlByShortUrlAsyc(shortUrl.toString())
                .thenApply(shortenedUrl -> {
                    if (shortenedUrl==null) throw new ShortenedUrlNotFoundException("Url is not found");
                    try {
                         return new URL(shortenedUrl.getLongUrl());
                    } catch (MalformedURLException e) {
                        throw new ShorteningServiceException("Can't construct URL from longUrl retrieved from the database",e);
                    }
                });
    }



}
