package app.services.shortening;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public  interface ShortenedUrlDao {

    public void getShortenedUrlByShortUrlAsyc(String ShortUrl, BiConsumer<ShortenedUrl, Throwable> callback);

    public void createShortenedUrlAsync(ShortenedUrl url, Consumer<Throwable> callback);
}
