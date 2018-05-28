package app.redirect;

import app.services.shortening.ShorteningService;
import io.javalin.Context;

import java.net.MalformedURLException;
import java.net.URL;

public class RedirectController {
    private ShorteningService shorteningService;

    public RedirectController(ShorteningService shorteningService) {
        this.shorteningService = shorteningService;
    }

    public void redirect(Context context) throws MalformedURLException {
        URL shortUrl = new URL(context.url());
        context.result(
                shorteningService
                        .resolveUrl(shortUrl).thenApply(longUrl -> {
                    context.redirect(longUrl.toString());
                    return "";
                }));

    }
}
