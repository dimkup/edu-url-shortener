package app.services.shortening;

import java.util.concurrent.CompletableFuture;

public  interface ShortenedUrlDao {

    CompletableFuture<ShortenedUrl> getShortenedUrlByShortUrlAsyc(String ShortUrl);

    CompletableFuture<Void> createShortenedUrlAsync(ShortenedUrl url);
}
