package app.api.v1.url;

import app.services.shortening.ShorteningService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Context;
import io.javalin.HaltException;

import java.net.MalformedURLException;
import java.net.URL;

public class ShortenedUrlController {
    private final ObjectMapper mapper = new ObjectMapper();
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
                                    try {
                                        return mapper.writeValueAsString(
                                                new CreateShortenedUrlResponse(request.getLongUrl(), shortUrl.toString())
                                        );
                                    } catch (JsonProcessingException e) {
                                        throw new HaltException(500, "Can't serialize response");
                                    }
                                }));
    }

    ;
}
