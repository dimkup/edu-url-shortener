package app.api.v1.url;

public class CreateShortenedUrlResponse {
    private String longUrl;
    private String shortUrl;

    public CreateShortenedUrlResponse() {
    }

    public CreateShortenedUrlResponse(String longUrl, String shortUrl) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
