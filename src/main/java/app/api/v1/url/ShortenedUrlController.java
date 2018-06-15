package app.api.v1.url;

import app.services.shortening.ShorteningService;
import io.javalin.Context;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Handles shortened URLs create and get requests
 */
public class ShortenedUrlController {
    private ShorteningService shorteningService;

    public ShortenedUrlController(ShorteningService shorteningService) {
        this.shorteningService = shorteningService;
    }


    public void createShortenedUrl(Context context) throws MalformedURLException {
        CreateShortenedUrlRequest request = context.bodyAsClass(CreateShortenedUrlRequest.class);
        URL longUrl = new URL(request.getLongUrl());
        context.result(
                shorteningService
                        .shortenUrl(longUrl)
                        .thenApply(
                                shortUrl -> {
                                    context.status(201).json(new CreateShortenedUrlResponse(request.getLongUrl(), shortUrl.toString()));
                                    return null;

                                }));
    }

    public void getShortenedUrl(Context context) throws MalformedURLException {
        URL shortUrl = new URL(context.queryParam("shortUrl"));
        context.result(
                shorteningService
                        .resolveUrl(shortUrl)
                        .thenApply(longUrl -> {
                            context.json(new CreateShortenedUrlResponse(longUrl.toString(), shortUrl.toString()));
                            return null;
                        })
        );
    }
}
