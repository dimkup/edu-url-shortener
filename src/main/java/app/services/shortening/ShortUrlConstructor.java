package app.services.shortening;

import app.services.shortening.exceptions.ShorteningServiceException;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * Constructs short URLs from the base URL and the hash
 */
public class ShortUrlConstructor {
    private Hasher hasher;
    private URL baseUrl;

    /**
     * Creates ShortUrlConstructor
     * @param baseUrl - base url part for all short URLs
     * @param hashLen - hash length in characters
     * @throws NoSuchAlgorithmException
     */
    public ShortUrlConstructor(URL baseUrl, int hashLen) throws NoSuchAlgorithmException {
        this.hasher = new Hasher(hashLen);
        this.baseUrl= baseUrl;
    }

    /**
     * Creates a short URL from the long one
     * @param longUrl
     * @return shortUrl
     */
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
