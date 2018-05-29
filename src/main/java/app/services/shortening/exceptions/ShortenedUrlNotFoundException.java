package app.services.shortening.exceptions;

/**
 * Notifies about missing short URL
 */
public class ShortenedUrlNotFoundException extends RuntimeException {
    public ShortenedUrlNotFoundException(String message) {
        super(message);
    }
}
