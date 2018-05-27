package app.services.shortening.exceptions;

import app.services.shortening.ShortenedUrl;

public class ShortenedUrlNotFoundException extends RuntimeException {
    public ShortenedUrlNotFoundException(String message) {
        super(message);
    }
}
