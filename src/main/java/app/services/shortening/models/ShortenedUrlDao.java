package app.services.shortening.models;

import java.util.concurrent.CompletableFuture;

/**
 * Shortened URL DAO interface
 */
public  interface ShortenedUrlDao {

    CompletableFuture<ShortenedUrl> getShortenedUrlByShortUrlAsyc(String ShortUrl);

    CompletableFuture<Void> createShortenedUrlAsync(ShortenedUrl url);
}
