package unit.shortening;

import app.services.shortening.Hasher;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.stream.Collectors;


public class TestHasher {

    @Test
    public void testHasherThrowsExceptionIfHashLengthIsWrong() throws NoSuchAlgorithmException {
        try {
            new Hasher(23);
            Assert.fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException anIllegalArgumentException) {
            Assert.assertEquals("Hash length must be less than 31 characters",anIllegalArgumentException.getMessage());
        }
        try {
            new Hasher(0);
            Assert.fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException anIllegalArgumentException) {
            Assert.assertEquals("Hash length must be less than 31 characters",anIllegalArgumentException.getMessage());
        }
    }

    @Test
    public void testHasherCalculatesHashForUrl() throws NoSuchAlgorithmException, MalformedURLException {
        Hasher hasher = new Hasher(10);
        URL url = new URL("https://www.google.com/");
        String hash = hasher.hashUrl(url);
        Assert.assertNotNull(hash);
        Assert.assertEquals(10,hash.length());
    }

    @Test
    public void testHasherUsesSalt() throws NoSuchAlgorithmException, MalformedURLException {
        Hasher hasher = new Hasher(10);
        URL url = new URL("https://www.google.com/");
        String hash1 = hasher.hashUrl(url);
        String hash2 = hasher.hashUrl(url);

        Assert.assertNotNull(hash1);
        Assert.assertNotNull(hash2);

        Assert.assertNotEquals(hash1,hash2);
    }

    @Test
    public void testHasherProducesSafeOutput() throws NoSuchAlgorithmException, MalformedURLException {
        Hasher hasher = new Hasher(22);
        URL url = new URL("https://www.google.com/");
        String hash = hasher.hashUrl(url);

        Set<Integer> validCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
                .chars().boxed().collect(Collectors.toSet());

        Assert.assertTrue(hash.chars().allMatch(validCharacters::contains));
    }
}
