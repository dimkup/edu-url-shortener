package app.services.shortening;

import app.services.shortening.exceptions.ShorteningServiceException;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class ShortUrlConstructor {
    private Hasher hasher;
    private URL baseUrl;
    public ShortUrlConstructor(URL baseUrl, int hashLen) throws NoSuchAlgorithmException {
        this.hasher = new Hasher(hashLen);
        this.baseUrl= baseUrl;
    }
    public URL generateShortUrl(URL longUrl) {
        String hash = hasher.hashUrl(longUrl);
        URL shortUrl;
        try {
            shortUrl = new URL(baseUrl,hash);

        } catch (MalformedURLException e) {
            throw new ShorteningServiceException("Can't construct short URL!",e);
        }
        return shortUrl;
    }
}
