package unit.shortening;

import app.services.shortening.ShortUrlConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class TestShortUrlConstructor {
    @Test
    public void testShortUrlConstructorConstruct() throws MalformedURLException, NoSuchAlgorithmException {
        final String BASE_URL = "http://exa.mpl";
        final String LONG_URL = "https://developer.ibm.com/code/open/projects/activity-streams/";
        URL baseUrl = new URL(BASE_URL);
        URL longUrl = new URL(LONG_URL);
        ShortUrlConstructor suc = new ShortUrlConstructor(baseUrl,6);
        URL shortUrl = suc.generateShortUrl(longUrl);

        Assert.assertEquals(baseUrl,new URL(shortUrl.getProtocol()+"://"+shortUrl.getAuthority()));
        Assert.assertEquals(7,shortUrl.getPath().length());

    }
}
