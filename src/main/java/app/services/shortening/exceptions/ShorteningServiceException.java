package app.services.shortening.exceptions;

import app.services.shortening.ShorteningService;

public class ShorteningServiceException  extends  RuntimeException {
    public ShorteningServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
