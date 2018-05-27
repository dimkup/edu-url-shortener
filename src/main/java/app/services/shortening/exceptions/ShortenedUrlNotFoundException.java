package app.services.shortening.exceptions;

public class ShortenedUrlNotFoundException extends RuntimeException {
    public ShortenedUrlNotFoundException(String message) {
        super(message);
    }
}
