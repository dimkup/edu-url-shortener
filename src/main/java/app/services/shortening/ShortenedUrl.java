package app.services.shortening;


import org.bson.types.ObjectId;

public class ShortenedUrl {
    private ObjectId id;
    private String shortUrl;
    private String longUrl;

    public ShortenedUrl() {}

    public ShortenedUrl(String shortUrl, String longUrl){
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(final ObjectId id) {
        this.id = id;
    }
}
