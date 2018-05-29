package app.services.shortening.exceptions;

/**
 * Generic exception for the ShorteningService
 */
public class ShorteningServiceException  extends  RuntimeException {
    public ShorteningServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
