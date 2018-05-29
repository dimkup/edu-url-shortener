package app.services.shortening;

import io.seruco.encoding.base62.Base62;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Generates a hash from the URL
 */
public class Hasher {
    private final int hashLength;
    private final MessageDigest md;
    private final Random random;
    private final Base62 base62;

    /**
     * Hasher constructor
     * @param hashLength desired length of the hash string
     * @throws NoSuchAlgorithmException
     */
    public Hasher(int hashLength) throws NoSuchAlgorithmException {
        this.hashLength = hashLength;
        if (hashLength<1||hashLength>20) throw new IllegalArgumentException("Hash length must be less than 31 characters");
        this.md = MessageDigest.getInstance("MD5");
        this.random = new Random();
        this.base62 = Base62.createInstance();
    }

    /**
     * Generates a hash from the URL
     * @param url - URL to hash
     * @return hash string
     */
    public String hashUrl(URL url) {
        byte[] b = (url+generateSalt()).getBytes();
        synchronized (md) {
            b = md.digest(b);
        }
        return new String(base62.encode(b)).substring(0,hashLength);
    }

    /**
     * Random salt generator
     * @return random string of characters
     */
    private String generateSalt() {
        return Integer.toHexString(random.nextInt());
    }
}
