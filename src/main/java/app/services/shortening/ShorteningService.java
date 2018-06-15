package app.services.shortening;

import app.config.ConfigShortening;
import app.services.shortening.exceptions.ShortenedUrlNotFoundException;
import app.services.shortening.exceptions.ShorteningServiceException;
import app.services.shortening.models.ShortenedUrl;
import app.services.shortening.models.ShortenedUrlDao;
import app.services.shortening.models.ShortenedUrlDaoImpl;
import com.mongodb.async.client.MongoDatabase;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

/**
 * Shortening service - Creates shortened URLs and resolves short ones to the long ones
 */
public class ShorteningService {
    private ShortenedUrlDao shortenedUrlDao;
    private ShortUrlConstructor shortUrlConstructor;

    /**
     * Shortening Service constructor
     * @param database - instance of MongoDatabase
     * @param config - shortening related configuration
     */
    public ShorteningService(MongoDatabase database, ConfigShortening config) {
        this.shortenedUrlDao = new ShortenedUrlDaoImpl(database);
        this.shortUrlConstructor = new ShortUrlConstructor(config.baseUrl(),config.hashLen());
    }

    //TODO handle hash collisions

    /**
     * Creates a shortened URL from the long one
     * @param longUrl
     * @return shortUrl
     */
    public CompletableFuture<URL> shortenUrl(URL longUrl) {
        URL shortUrl = shortUrlConstructor.generateShortUrl(longUrl);
        ShortenedUrl shortenedUrl = new ShortenedUrl(shortUrl.toString(),longUrl.toString());
        return shortenedUrlDao
                .createShortenedUrlAsync(shortenedUrl)
                .thenApply(v-> shortUrl);
    }

    /**
     * Looks for the long URL by the short one
     * @param shortUrl
     * @return longUrl
     */
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
