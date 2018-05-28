package app.api.v1.url;

public class CreateShortenedUrlRequest {
    private String longUrl;

    public CreateShortenedUrlRequest() {
    }

    public CreateShortenedUrlRequest(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
